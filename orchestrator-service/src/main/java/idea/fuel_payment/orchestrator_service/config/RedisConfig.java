package idea.fuel_payment.orchestrator_service.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisConfig {

	public static final String CACHE_FUEL_PRICES = "fuel-prices";

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		RedisSerializer<Object> jsonSerializer = jsonRedisSerializer();

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(jsonSerializer);
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(jsonSerializer);
		template.afterPropertiesSet();

		return template;
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisSerializer<Object> jsonSerializer = jsonRedisSerializer();

		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10))
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(
								new StringRedisSerializer()))
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
				.disableCachingNullValues();

		RedisCacheConfiguration fuelPricesConfig = defaultConfig
				.entryTtl(Duration.ofHours(5));

		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(defaultConfig)
				.withInitialCacheConfigurations(Map.of(CACHE_FUEL_PRICES, fuelPricesConfig))
				.build();
	}

	/**
	 * Custom JSON RedisSerializer với ObjectMapper tùy chỉnh
	 */
	private RedisSerializer<Object> jsonRedisSerializer() {
		ObjectMapper mapper = createObjectMapper();
		return new JsonRedisSerializer(mapper);
	}

	private ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();

		// Hỗ trợ Java 8 date/time
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// Cho phép serialize private fields
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

		// Bật type info để deserialize đúng class
		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType(Object.class)
				.build();
		mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY);

		return mapper;
	}

	/**
	 * Custom RedisSerializer sử dụng Jackson ObjectMapper
	 */
	static class JsonRedisSerializer implements RedisSerializer<Object> {

		private final ObjectMapper objectMapper;

		JsonRedisSerializer(ObjectMapper objectMapper) {
			this.objectMapper = objectMapper;
		}

		@Override
		public byte[] serialize(Object value) throws SerializationException {
			if (value == null) {
				return new byte[0];
			}
			try {
				return objectMapper.writeValueAsBytes(value);
			} catch (JsonProcessingException e) {
				throw new SerializationException("Error serializing object to JSON", e);
			}
		}

		@Override
		public Object deserialize(byte[] bytes) throws SerializationException {
			if (bytes == null || bytes.length == 0) {
				return null;
			}
			try {
				return objectMapper.readValue(bytes, Object.class);
			} catch (Exception e) {
				throw new SerializationException("Error deserializing JSON to object", e);
			}
		}
	}
}