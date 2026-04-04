package idea.fuel_payment.gas_service.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Common utility wrapping StringRedisTemplate.
 * Provides standardized Redis operations for the entire gas-service.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTool {

    private final StringRedisTemplate redisTemplate;

    public static String concatKey(String... args) {
	    return String.join("::", args);
    }

    // ───────── STRING ─────────
    public <T> void set(String key, T value) {
        set(key, toJson(value));
    }

    /** Write value (no TTL). */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        log.debug("[Redis] SET key={} value={}", key, value);
    }

    public void set(String key, Map<String, Object> value) {
        set(key, toJson(value));
    }

    /** Write value with TTL. */
    public void set(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
        log.debug("[Redis] SET key={} value={} ttl={}", key, value, ttl);
    }

    /** Read value. Returns Optional.empty() if key does not exist. */
    public Optional<String> get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        log.debug("[Redis] GET key={} -> {}", key, value);
        return Optional.ofNullable(value);
    }

    /** Delete key. */
    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("[Redis] DEL key={}", key);
    }

    // ───────── DISTRIBUTED LOCK (SET NX EX) ─────────

    /**
     * Try to acquire lock.
     * @return true if lock is acquired successfully (key does not exist).
     */
    public boolean tryLock(String lockKey, String requestId, Duration ttl) {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, ttl);
        boolean result = Boolean.TRUE.equals(acquired);
        log.debug("[Redis] LOCK key={} acquired={}", lockKey, result);
        return result;
    }

    /** Release lock — only if value matches (avoids accidental deletion of another request's lock). */
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

    public void hSet(String key, Map<String, ?> map) {
        Map<String, String> converted = map.entrySet().stream()
                .collect(Collectors.toMap(
		                Map.Entry::getKey,
                        e -> e.getValue().toString()
                ));
        redisTemplate.opsForHash().putAll(key, converted);
        log.debug("[Redis] HSET key={} map={}", key, converted);
    }

    /** HGET field. */
    public Optional<String> hGet(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        log.debug("[Redis] HGET key={} field={} -> {}", key, field, value);
        return Optional.ofNullable(value != null ? value.toString() : null);
    }

    public <T> Map<String, T> hGet(String key, Class<T> clazz) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        log.debug("[Redis] HGETALL key={} -> {}", key, entries);

        if (entries == null || entries.isEmpty()) {
            return Collections.emptyMap();
        }

        return entries.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> convertValue(e.getValue(), clazz)
                ));
    }
    private <T> T convertValue(Object value, Class<T> clazz) {
        if (value == null) return null;

        if (clazz.isAssignableFrom(value.getClass())) {
            return clazz.cast(value);
        }

        try {
            return new ObjectMapper().readValue(value.toString(), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Convert Redis value failed", e);
        }
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

    public static <T> String toJson(T any) {
        ObjectMapper objectMapper = new ObjectMapper();
	    try {
		    return objectMapper.writeValueAsString(any);
	    } catch (JsonProcessingException e) {
            String message = "Convert " + any.getClass() + "to JSON failed";
            throw new RuntimeException(message, e);
	    }
    }
}
