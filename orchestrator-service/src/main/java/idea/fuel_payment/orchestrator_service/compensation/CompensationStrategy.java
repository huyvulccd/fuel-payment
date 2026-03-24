package idea.fuel_payment.orchestrator_service.compensation;

import idea.fuel_payment.orchestrator_service.domain.enums.StepName;

@FunctionalInterface
public interface CompensationStrategy {

	void compensate(String sagaId, StepName stepName, String contextPayloadJson);
}
