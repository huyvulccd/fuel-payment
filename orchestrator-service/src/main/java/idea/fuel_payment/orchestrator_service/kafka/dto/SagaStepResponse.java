package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.StepName;

public record SagaStepResponse(
		String sagaId,
		StepName stepName,
		boolean success,
		String payloadJson,
		String errorMessage
) {
}
