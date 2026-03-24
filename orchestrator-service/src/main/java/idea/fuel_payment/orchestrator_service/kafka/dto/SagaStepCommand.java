package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.StepAction;
import idea.fuel_payment.orchestrator_service.domain.enums.StepName;

public record SagaStepCommand(
		String sagaId,
		StepName stepName,
		StepAction action,
		String payloadJson
) {
}
