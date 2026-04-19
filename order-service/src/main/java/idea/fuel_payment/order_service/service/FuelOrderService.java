package idea.fuel_payment.order_service.service;

import idea.fuel_payment.order_service.domain.common.OrderCodeGenerator;
import idea.fuel_payment.order_service.domain.common.RedisTool;
import idea.fuel_payment.order_service.domain.common.UtilityService;
import idea.fuel_payment.order_service.domain.enity.FuelOrder;
import idea.fuel_payment.order_service.outbox.OutboxEvent;
import idea.fuel_payment.order_service.domain.enity.Vehicle;
import idea.fuel_payment.order_service.dto.fuel_order.FuelOrderResponse;
import idea.fuel_payment.order_service.dto.fuel_order.FuelOrderUpdateRequest;
import idea.fuel_payment.order_service.dto.fuel_order.JoinLineRequest;
import idea.fuel_payment.order_service.dto.fuel_order.JoinLineResponse;
import idea.fuel_payment.order_service.dto.kafka.OrderCreatedEvent;
import idea.fuel_payment.order_service.repository.FuelOrderRepository;
import idea.fuel_payment.order_service.outbox.OutboxEventRepository;
import idea.fuel_payment.order_service.repository.VehicleRepository;
import idea.fuel_payment.order_service.service.producer.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Service for handling fuel order business logic.
 *
 * @author order-service
 * @version 2026/04/04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FuelOrderService extends UtilityService {
    private final FuelOrderRepository fuelOrderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final VehicleRepository vehicleRepository;
    private final RedisTool redisTool;
    private final RestTemplate restTemplate;

    private final OrderEventProducer orderEventProducer;

    @Value("${gas-service.base-url:http://localhost:8081}")
    private String gasServiceBaseUrl;

    /**
     * Join the fueling line at a gas station.
     * 1. Look up vehicle by license plate → get owner, fuelType
     * 2. Get current fuel price from Redis
     * 3. Auto-assign an available pump from gas-service
     * 4. Create FuelOrder (status = CREATED)
     * 5. Save OutboxEvent (for Kafka → orchestrator SAGA)
     * 6. Return JoinLineResponse
     *
     * @param request join-line request
     * @return join-line response with order details
     */
    @Transactional
    public JoinLineResponse joinLine(final JoinLineRequest request) {
        // 1. Look up vehicle
        Vehicle vehicle = vehicleRepository.findOneByLicensePlate(request.licensePlate())
                .orElseThrow(() -> new NoSuchElementException(
                        "Vehicle not found: " + request.licensePlate()));

        String fuelType = vehicle.getVehicleModel().getFuelType();
        if (isEmpty(fuelType)) {
            throw new IllegalStateException(
                    "Vehicle model has no fuel type configured: " + vehicle.getVehicleModel().getName());
        }

        // 2. Get current fuel price from Redis
        BigDecimal unitPrice = getValue(
                redisTool.hGet(RedisTool.CURRENT_FUEL_PRICE, fuelType), BigDecimal.class);
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Fuel price not found for type: " + fuelType);
        }

        // 3. Auto-assign available pump from gas-service (REST call)
        Long pumpId = findAvailablePump(request.stationId(), fuelType);

        // 4. Generate order code and create FuelOrder
        String orderCode = OrderCodeGenerator.generate();

        FuelOrder savedOrder = createFuelOrder(request, orderCode, vehicle, pumpId, fuelType, unitPrice);

        // 5. Create OutboxEvent for SAGA
        Map<String, Object> eventPayload = new HashMap<>();
        eventPayload.put("orderCode", orderCode);
        eventPayload.put("licensePlate", request.licensePlate());
        Long ownerId = vehicle.getOwner().getId();
        eventPayload.put("ownerId", ownerId);
        eventPayload.put("stationId", request.stationId());
        eventPayload.put("pumpId", pumpId);
        eventPayload.put("fuelType", fuelType);
        String payload = toJson(eventPayload, "Failed to serialize OrderCreatedEvent");

        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("FuelOrder")
                .aggregateId(savedOrder.getId() != null ? savedOrder.getId().toString() : orderCode)
                .eventType("OrderCreated")
                .payload(payload)
                .status(OutboxEvent.OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);

        log.info("OutboxEvent created for order: {}", orderCode);


        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderCode(orderCode)
                .licensePlate(request.licensePlate())
                .ownerId(ownerId)
                .stationId(request.stationId())
                .pumpId(pumpId)
                .fuelType(fuelType)
                .createdAt(now())
                .build();

        orderEventProducer.sendOrderCreatedEvent(orderCreatedEvent);
        // 6. Return response
        return new JoinLineResponse(
                orderCode,
                request.licensePlate(),
                request.stationId(),
                pumpId,
                fuelType,
                unitPrice,
                List.of("Successfully joined the fueling line")
        );
    }

    private FuelOrder createFuelOrder(JoinLineRequest request, String orderCode, Vehicle vehicle, Long pumpId, String fuelType, BigDecimal unitPrice) {
        FuelOrder order = new FuelOrder();
        order.setOrderCode(orderCode);
        order.setVehicle(vehicle);
        order.setLicensePlate(request.licensePlate());
        order.setStationId(request.stationId());
        order.setPumpId(pumpId);
        order.setFuelType(FuelOrder.FuelType.valueOf(fuelType));
        order.setUnitPrice(unitPrice);
        order.setOrderStatus(FuelOrder.OrderStatus.CREATED);

        FuelOrder savedOrder = fuelOrderRepository.save(order);
        log.info("Created fuel order: {} for vehicle: {} at station: {}",
                orderCode, request.licensePlate(), request.stationId());
        return savedOrder;
    }

    /**
     * Get all fuel orders for an owner.
     */
    public List<FuelOrderResponse> getFuelOrdersByOwnerId(final Long ownerId) {
        return fuelOrderRepository.findByVehicle_Owner_IdOrderByCreatedAtDesc(ownerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get a single fuel order by order code.
     */
    public FuelOrderResponse getFuelOrder(final String orderCode) {
        FuelOrder order = fuelOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderCode));
        return mapToResponse(order);
    }

    /**
     * Update fuel order status.
     */
    @Transactional
    public FuelOrderResponse updateFuelOrder(final String orderCode, final FuelOrderUpdateRequest request) {
        FuelOrder order = fuelOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderCode));

        order.setOrderStatus(FuelOrder.OrderStatus.valueOf(request.status()));
        FuelOrder saved = fuelOrderRepository.save(order);
        log.info("Updated order {} status to {}", orderCode, request.status());

        return mapToResponse(saved);
    }

    // =====================================================
    //  PRIVATE METHODS
    // =====================================================

    /**
     * Call gas-service REST API to find an available pump.
     */
    private Long findAvailablePump(final Long stationId, final String fuelType) {
        String url = gasServiceBaseUrl + "/api/v1/fuel-pumps/available?stationId="
                + stationId + "&fuelType=" + fuelType;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || response.get("id") == null) {
                throw new IllegalStateException("No available pump at station " + stationId);
            }
            return Long.valueOf(response.get("id").toString());
        } catch (Exception e) {
            log.error("Failed to find available pump at station {}: {}", stationId, e.getMessage());
            throw new IllegalStateException(
                    "Cannot find available pump at station " + stationId + " for " + fuelType, e);
        }
    }

    private FuelOrderResponse mapToResponse(final FuelOrder order) {
        return new FuelOrderResponse(
                order.getOrderCode(),
                order.getLicensePlate(),
                order.getStationId(),
                order.getPumpId(),
                order.getFuelType().name(),
                order.getUnitPrice(),
                order.getQuantityLiters(),
                order.getTotalAmount(),
                order.getOrderStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
