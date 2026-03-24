package idea.fuel_payment.orchestrator_service.lock.impl;

import idea.fuel_payment.orchestrator_service.lock.SagaLockManager;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Profile("!test")
public class RedisSagaLockManager implements SagaLockManager {

	private static final String LOCK_PREFIX = "saga:lock:";

	private final StringRedisTemplate redisTemplate;

	public RedisSagaLockManager(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public boolean tryLock(String key, Duration ttl) {
		Boolean ok = redisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key, "1", ttl);
		return Boolean.TRUE.equals(ok);
	}

	@Override
	public void unlock(String key) {
		redisTemplate.delete(LOCK_PREFIX + key);
	}
}
