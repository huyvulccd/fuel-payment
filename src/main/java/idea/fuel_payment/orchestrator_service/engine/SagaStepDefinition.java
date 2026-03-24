package idea.fuel_payment.orchestrator_service.engine;

import idea.fuel_payment.orchestrator_service.domain.enums.StepAction;
import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import lombok.Builder;

@Builder
public record SagaStepDefinition(
		int stepOrder,
		StepName stepName,
		StepAction forwardAction,
		StepAction compensationAction,
		String targetTopic,
		int timeoutSeconds,
		boolean compensable,
		boolean asyncWait // step need wait
) {
}
