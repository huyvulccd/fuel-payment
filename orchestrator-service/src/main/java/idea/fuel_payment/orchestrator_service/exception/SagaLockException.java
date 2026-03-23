package idea.fuel_payment.orchestrator_service.exception;

public class SagaLockException extends SagaException {

	public SagaLockException(String message) {
		super(message);
	}

	public SagaLockException(String message, Throwable cause) {
		super(message, cause);
	}
}
