package idea.fuel_payment.orchestrator_service.exception;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaStepLog;

public class StepExecutionException extends SagaException {

	private final SagaStepLog.StepName stepName;

	public StepExecutionException(SagaStepLog.StepName stepName, String message) {
		super(message);
		this.stepName = stepName;
	}

	public StepExecutionException(SagaStepLog.StepName stepName, String message, Throwable cause) {
		super(message, cause);
		this.stepName = stepName;
	}

	public SagaStepLog.StepName getStepName() {
		return stepName;
	}
}
