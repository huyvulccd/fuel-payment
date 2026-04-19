package idea.fuel_payment.attention_service.kafka;

import idea.fuel_payment.attention_service.outbox.OutboxService;
import idea.fuel_payment.attention_service.kafka.dto.SagaStepCommand;
import idea.fuel_payment.attention_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.attention_service.kafka.dto.StepAction;
import idea.fuel_payment.attention_service.kafka.dto.StepStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendNotificationConsumer {

	private final OutboxService outboxService;

	@KafkaListener(
			topics = "${kafka.topics.saga-step-command}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	@Transactional
	public void consume(SagaStepCommand command) {
		// Step 8: SEND_NOTIFICATION
		if (command.getAction() != StepAction.SEND_NOTIFICATION) {
			return;
		}

		log.info("Received SEND_NOTIFICATION command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			// SKELETON: Business Logic for sending notification
			// Send SMS, Push, or Email
			log.info("Sending notification for order: {}", command.getOrderCode());
			
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
					"SEND_NOTIFICATION_SUCCESS",
					response
			);
			
		} catch (Exception e) {
			log.error("Error processing send notification step", e);
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
					"SEND_NOTIFICATION_FAILED",
					response
			);
		}
	}
}
