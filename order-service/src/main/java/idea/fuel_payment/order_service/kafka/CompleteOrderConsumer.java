package idea.fuel_payment.order_service.kafka;

import idea.fuel_payment.order_service.outbox.OutboxService;
import idea.fuel_payment.order_service.kafka.dto.SagaStepCommand;
import idea.fuel_payment.order_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.order_service.kafka.dto.StepAction;
import idea.fuel_payment.order_service.kafka.dto.StepStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompleteOrderConsumer {

	private final OutboxService outboxService;

	@KafkaListener(
			topics = "${kafka.topics.saga-step-command}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	@Transactional
	public void consume(SagaStepCommand command) {
		// Step 7: COMPLETE_ORDER
		if (command.getAction() != StepAction.COMPLETE_ORDER) {
			return;
		}

		log.info("Received COMPLETE_ORDER command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			// SKELETON: Business Logic for Order Completion
			// Mark order status as COMPLETED
			log.info("Completing order: {}", command.getOrderCode());
			
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
					"COMPLETE_ORDER_SUCCESS",
					response
			);
			
		} catch (Exception e) {
			log.error("Error processing complete order step", e);
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
					"COMPLETE_ORDER_FAILED",
					response
			);
		}
	}
}
