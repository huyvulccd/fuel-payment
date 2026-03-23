package idea.fuel_payment.orchestrator_service.exception;

import idea.fuel_payment.orchestrator_service.domain.enums.StepName;

public class StepExecutionException extends SagaException {

	private final StepName stepName;

	public StepExecutionException(StepName stepName, String message) {
		super(message);
		this.stepName = stepName;
	}

	public StepExecutionException(StepName stepName, String message, Throwable cause) {
		super(message, cause);
		this.stepName = stepName;
	}

	public StepName getStepName() {
		return stepName;
	}
}
