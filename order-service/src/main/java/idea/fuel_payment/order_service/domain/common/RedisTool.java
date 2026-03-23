package idea.fuel_payment.order_service.domain.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
}
