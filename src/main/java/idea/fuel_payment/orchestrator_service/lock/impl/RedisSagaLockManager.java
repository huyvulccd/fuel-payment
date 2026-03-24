package idea.fuel_payment.orchestrator_service.lock.impl;

import idea.fuel_payment.orchestrator_service.lock.SagaLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSagaLockManager implements SagaLockManager {

	private final StringRedisTemplate redisTemplate;

	@Value("${saga.lock.prefix:saga:lock:}")
	private String lockPrefix;

	@Value("${saga.lock.timeout-seconds:300}")
	private long timeoutSeconds;

	@Override
	public boolean acquireLock(String orderCode) {
		String key = lockPrefix + orderCode;
		Boolean success = redisTemplate.opsForValue()
				.setIfAbsent(key, "LOCKED",
						Duration.ofSeconds(timeoutSeconds));

		boolean acquired = Boolean.TRUE.equals(success);
		log.info("Lock {} for order {}: {}",
				acquired ? "acquired" : "failed",
				orderCode, key);
		return acquired;
	}

	@Override
	public void releaseLock(String orderCode) {
		String key = lockPrefix + orderCode;
		Boolean deleted = redisTemplate.delete(key);
		log.info("Lock released for order {}: {}",
				orderCode, deleted);
	}

	@Override
	public boolean isLocked(String orderCode) {
		String key = lockPrefix + orderCode;
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	@Override
	public boolean renewLock(String orderCode) {
		String key = lockPrefix + orderCode;
		return Boolean.TRUE.equals(
				redisTemplate.expire(key,
						Duration.ofSeconds(timeoutSeconds))
		);
	}
}
