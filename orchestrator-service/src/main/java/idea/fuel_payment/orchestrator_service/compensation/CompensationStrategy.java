package idea.fuel_payment.orchestrator_service.compensation;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaStepLog;

@FunctionalInterface
public interface CompensationStrategy {

	void compensate(String sagaId, SagaStepLog.StepName stepName, String contextPayloadJson);
}
