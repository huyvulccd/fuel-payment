package idea.fuel_payment.orchestrator_service.lock;

public interface SagaLockManager {
	boolean acquireLock(String orderCode);

	void releaseLock(String orderCode);

	boolean isLocked(String orderCode);

	boolean renewLock(String orderCode);
}
