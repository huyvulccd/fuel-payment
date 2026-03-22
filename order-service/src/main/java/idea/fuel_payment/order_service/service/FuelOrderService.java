package idea.fuel_payment.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.fuel_payment.order_service.domain.enity.FuelOrder;
import idea.fuel_payment.order_service.domain.enity.OutboxEvent;
import idea.fuel_payment.order_service.repository.FuelOrderRepository;
import idea.fuel_payment.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import idea.fuel_payment.order_service.dto.fuel_order.FuelOrderResponse;
import idea.fuel_payment.order_service.dto.fuel_order.FuelOrderUpdateRequest;
import idea.fuel_payment.order_service.dto.fuel_order.JoinLineRequest;
import idea.fuel_payment.order_service.dto.fuel_order.JoinLineResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelOrderService {

    private final FuelOrderRepository fuelOrderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Demonstrates the Transactional Outbox Pattern:
     * Saves the domain entity AND the outbox event in the same atomic database transaction.
     */
    @Transactional
    public FuelOrder createFuelOrder(FuelOrder fuelOrder) {
        // 1. Save the actual business entity
        FuelOrder savedOrder = fuelOrderRepository.save(fuelOrder);

        // 2. Create the JSON payload for the event
        String payload;
        try {
            payload = objectMapper.writeValueAsString(savedOrder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize FuelOrder", e);
        }

        // 3. Save the OutboxEvent in the same transaction
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("FuelOrder")
                .aggregateId(savedOrder.getId() != null ? savedOrder.getId().toString() : "UNKNOWN")
                .eventType("OrderCreated")
                .payload(payload)
                .status(OutboxEvent.OutboxStatus.PENDING)
                .build();
                
        outboxEventRepository.save(event);

        return savedOrder;
    }

    public List<FuelOrderResponse> getFuelOrdersByOwnerId(Long ownerId) {
        // TODO: Implement logic
        return List.of();
    }

    public FuelOrderResponse getFuelOrder(String orderCode) {
        // TODO: Implement logic
        return null;
    }

    public JoinLineResponse joinLine(JoinLineRequest request) {
        // TODO: Implement logic
        return null;
    }

    public FuelOrderResponse updateFuelOrder(String orderCode, FuelOrderUpdateRequest request) {
        // TODO: Implement logic
        return null;
    }
}
