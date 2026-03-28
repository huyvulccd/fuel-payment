package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.StepStatus;

import java.util.Map;

public record SagaStepResponse(
		String sagaId,
		String orderCode,
		Integer stepOrder,
		StepStatus status,
		Map<String, Object> payload,
		String errorMessage,
		Long timestamp
) {
}
