package idea.fuel_payment.orchestrator_service.exception;

public class SagaLockException extends SagaException {

	public SagaLockException(String orderCode) {
		super("Cannot acquire lock for order: " + orderCode);
	}
}
