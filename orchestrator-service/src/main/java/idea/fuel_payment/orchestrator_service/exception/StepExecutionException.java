package idea.fuel_payment.orchestrator_service.exception;

public class StepExecutionException extends SagaException {

	private final String sagaId;
	private final int stepOrder;

	public StepExecutionException(String sagaId, int stepOrder,
								  String message) {
		super(String.format("Step %d failed for saga %s: %s",
				stepOrder, sagaId, message));
		this.sagaId = sagaId;
		this.stepOrder = stepOrder;
	}
}
