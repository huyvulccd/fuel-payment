package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.StepAction;
import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import lombok.Builder;

import java.util.Map;

@Builder
public record SagaStepCommand(
		String sagaId,
		String orderCode,
		Integer stepOrder,
		StepAction action,
		Map<String, Object>payload,
		Long timestamp
) {
	public static SagaStepCommand of(
			String sagaId,
			String orderCode,
			Integer stepOrder,
			StepAction action,
			Map<String, Object> payload) {
		return SagaStepCommand.builder()
				.sagaId(sagaId)
				.orderCode(orderCode)
				.stepOrder(stepOrder)
				.action(action)
				.payload(payload)
				.timestamp(System.currentTimeMillis())
				.build();
	}
}
