package idea.fuel_payment.gas_service.kafka;

import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionRegisterRequest;
import idea.fuel_payment.gas_service.kafka.dto.SagaStepCommand;
import idea.fuel_payment.gas_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.gas_service.kafka.dto.StepStatus;
import idea.fuel_payment.gas_service.outbox.OutboxService;
import idea.fuel_payment.gas_service.service.FuelPumpService;
import idea.fuel_payment.gas_service.service.PumpSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Single Kafka consumer for all SAGA step commands targeting the gas-service.
 * Dispatches to the appropriate handler based on StepAction.
 *
 * This avoids Kafka partition competition that occurs when multiple @KafkaListener
 * methods in the same consumer group listen to the same topic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GasSagaCommandConsumer {

	private final OutboxService outboxService;
	private final FuelPumpService fuelPumpService;
	private final PumpSessionService pumpSessionService;

	@KafkaListener(
			topics = "${kafka.topics.saga-step-command}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	@Transactional
	public void consume(SagaStepCommand command) {
		switch (command.getAction()) {
			case ACTIVATE_PUMP -> handleActivatePump(command);
			case WAIT_PUMP_COMPLETE -> handleWaitPumpComplete(command);
			case UPDATE_INVENTORY -> handleUpdateInventory(command);
			default -> {
				// Not for this service, ignore
				log.debug("Ignoring command with action: {}", command.getAction());
			}
		}
	}

	// ======================== ACTIVATE_PUMP (Step 3) ========================

	private void handleActivatePump(SagaStepCommand command) {
		log.info("Received ACTIVATE_PUMP command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			Map<String, Object> payload = command.getPayload();
			log.debug("Payload in active pump step: {}", payload);

			Long pumpId = Long.valueOf(payload.get("pumpId").toString());
			BigDecimal unitPrice = BigDecimal.ZERO;
			String licensePlate = payload.get("licensePlate").toString();
//			FuelPumpResponse fuelPumpResponse = fuelPumpService.activatePump(pumpId);

//			PumpSessionRegisterRequest pumpSessionRegisterRequest = PumpSessionRegisterRequest
//					.builder()
//					.pumpId(pumpId)
//					.unitPrice(unitPrice)
//					.fuelType(fuelPumpResponse.fuelType())
//					.licensePlate(licensePlate)
//					.quantityLiters(unitPrice)
//					.totalAmount(BigDecimal.ZERO)
//					.orderCode(command.getOrderCode())
//					.build();
//			PumpSessionResponse pumpSessionResponse = pumpSessionService.registerSession(pumpSessionRegisterRequest);

			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(StepStatus.SUCCESS)
					.payload(null)
					.timestamp(System.currentTimeMillis())
					.build();

			// SAVE TO OUTBOX
			outboxService.saveEvent(
					"SAGA_RESPONSE",
					command.getSagaId(),
					"ACTIVATE_PUMP_SUCCESS",
					response
			);

		} catch (Exception e) {
			log.error("Error processing active pump step", e);
			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(StepStatus.FAILED)
					.errorMessage(e.getMessage())
					.timestamp(System.currentTimeMillis())
					.build();

			outboxService.saveEvent(
					"SAGA_RESPONSE",
					command.getSagaId(),
					"ACTIVATE_PUMP_FAILED",
					response
			);
		}
	}

	// ======================== WAIT_PUMP_COMPLETE (Step 4) ========================

	private void handleWaitPumpComplete(SagaStepCommand command) {
		log.info("Received WAIT_PUMP_COMPLETE command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			// ASYNC WAIT: Instead of sending SUCCESS immediately, 
			// we register the session and WAIT for an external event (pump hardware completion).
			log.info("ASYNC WAIT: Registering pump session for order: {} (Saga: {})", 
					command.getOrderCode(), command.getSagaId());

			Map<String, Object> payload = command.getPayload();
			Long pumpId = Long.valueOf(payload.get("pumpId").toString());
			
			// Mock registration of session with Saga context
			PumpSessionRegisterRequest sessionRequest = PumpSessionRegisterRequest.builder()
					.pumpId(pumpId)
					.orderCode(command.getOrderCode())
					.licensePlate(payload.containsKey("licensePlate") ? payload.get("licensePlate").toString() : "UNKNOWN")
					.fuelType(idea.fuel_payment.gas_service.domain.enums.FuelType.valueOf(payload.get("fuelType").toString()))
					.quantityLiters(BigDecimal.ZERO) 
					.unitPrice(new BigDecimal(payload.get("unitPrice").toString()))
					.totalAmount(BigDecimal.ZERO)
					.sagaId(command.getSagaId())
					.stepOrder(command.getStepOrder())
					.build();

			pumpSessionService.registerSession(sessionRequest);
			
			log.info("Saga thread released. Waiting for external completion API for order: {}", command.getOrderCode());

		} catch (Exception e) {
			log.error("Error processing wait pump complete step", e);
			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(StepStatus.FAILED)
					.errorMessage(e.getMessage())
					.timestamp(System.currentTimeMillis())
					.build();

			outboxService.saveEvent(
					"SAGA_RESPONSE",
					command.getSagaId(),
					"WAIT_PUMP_COMPLETE_FAILED",
					response
			);
		}
	}

	// ======================== UPDATE_INVENTORY (Step 6) ========================

	private void handleUpdateInventory(SagaStepCommand command) {
		log.info("Received UPDATE_INVENTORY command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			// SKELETON: Business Logic for Inventory Update
			log.info("Updating inventory for order: {}", command.getOrderCode());

			// Mock business logic
			boolean success = true;

			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(StepStatus.SUCCESS)
					.timestamp(System.currentTimeMillis())
					.build();

			// SAVE TO OUTBOX
			outboxService.saveEvent(
					"SAGA_RESPONSE",
					command.getSagaId(),
					"UPDATE_INVENTORY_SUCCESS",
					response
			);

		} catch (Exception e) {
			log.error("Error processing update inventory step", e);
			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(StepStatus.FAILED)
					.errorMessage(e.getMessage())
					.timestamp(System.currentTimeMillis())
					.build();

			outboxService.saveEvent(
					"SAGA_RESPONSE",
					command.getSagaId(),
					"UPDATE_INVENTORY_FAILED",
					response
			);
		}
	}
}
