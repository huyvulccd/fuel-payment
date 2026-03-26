package idea.fuel_payment.order_service.domain.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTool {
	public static final String CURRENT_FUEL_PRICE = "fuel:price:";

	private final StringRedisTemplate redisTemplate;

	public BigDecimal getBigDecimal(String key) {
		String value = redisTemplate.opsForValue().get(key);
		log.error("key [{}] can not get value ", key);
		return (value != null) ? new BigDecimal(value) : new BigDecimal("0");
	}
	// ───────── STRING ─────────

	/** Ghi value (không TTL). */
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		log.debug("[Redis] SET key={} value={}", key, value);
	}

	public void set(String key, Map<String, Object> value) {
		set(key, toJson(value));
	}

	/** Ghi value với TTL. */
	public void set(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
		log.debug("[Redis] SET key={} value={} ttl={}", key, value, ttl);
	}

	/** Đọc value. Trả về Optional.empty() nếu key không tồn tại. */
	public Optional<String> get(String key) {
		String value = redisTemplate.opsForValue().get(key);
		log.debug("[Redis] GET key={} -> {}", key, value);
		return Optional.ofNullable(value);
	}

	/** Xóa key. */
	public void delete(String key) {
		redisTemplate.delete(key);
		log.debug("[Redis] DEL key={}", key);
	}

	// ───────── DISTRIBUTED LOCK (SET NX EX) ─────────

	/**
	 * Cố gắng acquire lock.
	 * @return true nếu lock được acquire thành công (key chưa tồn tại).
	 */
	public boolean tryLock(String lockKey, String requestId, Duration ttl) {
		Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, ttl);
		boolean result = Boolean.TRUE.equals(acquired);
		log.debug("[Redis] LOCK key={} acquired={}", lockKey, result);
		return result;
	}

	/** Release lock — chỉ xóa nếu value khớp (tránh xóa nhầm lock của request khác). */
	public void releaseLock(String lockKey, String requestId) {
		String current = redisTemplate.opsForValue().get(lockKey);
		if (requestId.equals(current)) {
			redisTemplate.delete(lockKey);
			log.debug("[Redis] RELEASE LOCK key={}", lockKey);
		} else {
			log.warn("[Redis] RELEASE LOCK skipped — key={} owner mismatch", lockKey);
		}
	}

	// ───────── HASH ─────────

	/** HSET field. */
	public void hSet(String key, String field, String value) {
		redisTemplate.opsForHash().put(key, field, value);
		log.debug("[Redis] HSET key={} field={} value={}", key, field, value);
	}

	/** HGET field. */
	public Optional<String> hGet(String key, String field) {
		Object value = redisTemplate.opsForHash().get(key, field);
		log.debug("[Redis] HGET key={} field={} -> {}", key, field, value);
		return Optional.ofNullable(value != null ? value.toString() : null);
	}

	public static Map<String, Object> toMap(String str) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(str, new TypeReference<>() {
			});
		} catch (Exception e) {
			throw new RuntimeException("Invalid JSON", e);
		}
	}

	public static String toJson(Map<String, Object> map) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			throw new RuntimeException("Convert map to JSON failed", e);
		}
	}
}
