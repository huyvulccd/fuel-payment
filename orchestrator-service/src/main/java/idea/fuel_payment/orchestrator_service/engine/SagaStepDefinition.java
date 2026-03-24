package idea.fuel_payment.orchestrator_service.engine;

import idea.fuel_payment.orchestrator_service.domain.enums.StepName;

public record SagaStepDefinition(
		int order,
		StepName stepName,
		String targetService
) {
}
