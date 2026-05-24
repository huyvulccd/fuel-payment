package idea.fuel_payment.payment_service.kafka;

import idea.fuel_payment.payment_service.kafka.dto.SagaStepCommand;
import idea.fuel_payment.payment_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.payment_service.kafka.dto.StepStatus;
import idea.fuel_payment.payment_service.outbox.OutboxService;
import idea.fuel_payment.payment_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Single Kafka consumer for all SAGA step commands targeting the payment-service.
 * Dispatches to the appropriate handler based on StepAction.
 * <p>
 * This avoids Kafka partition competition that occurs when multiple @KafkaListener
 * methods in the same consumer group listen to the same topic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaCommandConsumer {

	private final BalanceService balanceService;
	private final OutboxService outboxService;

	@Value("${kafka.topics.saga-step-response}")
	private String topicResponse;

	@KafkaListener(
			topics = "${kafka.topics.saga-step-command:fuel.saga.step.command}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	@Transactional
	public void consume(SagaStepCommand command) {
		switch (command.getAction()) {
			case CHECK_BALANCE -> handleCheckBalance(command);
			case PROCESS_PAYMENT -> handleProcessPayment(command);
			default -> // Not for this service, ignore
					log.debug("Ignoring command with action: {}", command.getAction());
		}
	}

	// ======================== CHECK_BALANCE (Step 2) ========================

	private void handleCheckBalance(SagaStepCommand command) {
		log.info("Received CHECK_BALANCE command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			Map<String, Object> payload = command.getPayload();
			Long ownerId = Long.valueOf(payload.get("ownerId").toString());

			// Check if owner exists and has non-negative balance
			boolean hasFunds = balanceService.hasSufficientBalance(ownerId, BigDecimal.ZERO);

			StepStatus status = hasFunds ? StepStatus.SUCCESS : StepStatus.FAILED;
			String error = hasFunds ? null : "Insufficient balance or user not found";

			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(status)
					.errorMessage(error)
					.timestamp(System.currentTimeMillis())
					.build();

			// SAVE TO OUTBOX
			outboxService.saveEvent(
					"SAGA_RESPONSE",
					command.getSagaId(),
					topicResponse,
					response
			);

		} catch (Exception e) {
			log.error("Error processing check balance step", e);
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
					topicResponse,
					response
			);
		}
	}

	// ======================== PROCESS_PAYMENT (Step 5) ========================

	private void handleProcessPayment(SagaStepCommand command) {
		log.info("Received PROCESS_PAYMENT command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			// SKELETON: Business Logic for Payment Processing
			// 1. Deduct balance from owner
			// 2. Create transaction record
			Object amountObj = command.getPayload().getOrDefault("totalAmount",
					command.getPayload().get("amount"));

			log.info("Processing payment for order: {} with amount: {}",
					command.getOrderCode(), amountObj);

			// Mock business logic

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
					topicResponse,
					response
			);

		} catch (Exception e) {
			log.error("Error processing payment step", e);
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
					topicResponse,
					response
			);
		}
	}
}
