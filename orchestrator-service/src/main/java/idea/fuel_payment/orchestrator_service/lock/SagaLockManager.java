package idea.fuel_payment.orchestrator_service.lock;

import java.time.Duration;

public interface SagaLockManager {

	boolean tryLock(String key, Duration ttl);

	void unlock(String key);
}
