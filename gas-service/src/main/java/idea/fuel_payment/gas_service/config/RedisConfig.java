package idea.fuel_payment.gas_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig {

    /** Cache name for current price fuels. */
    public static final String CACHE_FUEL_PRICES = "fuel-prices";

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * RedisCacheManager — used for @Cacheable / @CacheEvict in the service layer.
     * Serialize values to JSON (supports LocalDateTime, BigDecimal, etc.).
     * Default TTL: 10 minutes. "fuel-prices" cache: 5 hours (fuel prices change infrequently).
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules(); // hỗ trợ LocalDateTime
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Use clean JSON (no @class/type info)
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(om, Object.class);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        // only "fuel-prices" cache TTL: 5 hours
        RedisCacheConfiguration fuelPricesConfig = defaultConfig
                .entryTtl(Duration.ofMinutes(300));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(Map.of(CACHE_FUEL_PRICES, fuelPricesConfig))
                .build();
    }
}
