package idea.fuel_payment.order_service.domain.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public abstract class UtilityService {
	protected static <T> boolean isEmpty(T obj) {
		switch (obj) {
			case null -> {
				return true;
			}

			// String
			case String str -> {
				return str.isEmpty();
			}


			// CharSequence (StringBuilder, StringBuffer, etc.)
			case CharSequence cs -> {
				return cs.isEmpty();
			}


			// Collection (List, Set, Queue, etc.)
			case Collection<?> collection -> {
				return collection.isEmpty();
			}


			// Map
			case Map<?, ?> map -> {
				return map.isEmpty();
			}


			// Optional
			case Optional<?> optional -> {
				return optional.isEmpty();
			}
			default -> {
			}
		}

		// Array
		if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		}

		throw new IllegalArgumentException();
	}

	public static boolean checkLength(String str, int shortest, int longest) {
		if (isEmpty(str))
			return false;

		return shortest <= str.length() && str.length() <= longest;

	}

	public static <T> boolean isNotEmpty(T obj) {
		return !isEmpty(obj);
	}

	protected boolean validateEmail(String email) {
		if (isEmpty(email))
			return false;
		String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
		return email.matches(regex);
	}

	protected BigDecimal toBigDecimal(Object input) {
		if (input == null) {
			return null;
		}

		if (input instanceof BigDecimal) {
			return (BigDecimal) input;
		}

		if (input instanceof Integer || input instanceof Long) {
			return BigDecimal.valueOf(((Number) input).longValue());
		}

		if (input instanceof Double || input instanceof Float) {
			return new BigDecimal(input.toString());
		}

		if (input instanceof Number) {
			return new BigDecimal(input.toString());
		}

		if (input instanceof String) {
			String str = ((String) input).trim();
			if (str.isEmpty()) {
				return null;
			}
			try {
				return new BigDecimal(str);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Cannot convert to BigDecimal: " + input, e);
			}
		}

		throw new IllegalArgumentException("Unsupported type: " + input.getClass());
	}
	@SuppressWarnings("unchecked")
	protected <T> T getValue(Object o, Class<T> clazz) {
		if (o == null) {
			return null;
		}

		if (o instanceof Optional<?> opt) {
			if (opt.isEmpty())
				return null;
			o = opt.get();
		}

		if (clazz.isInstance(o)) {
			return (T) o;
		}

		// BigDecimal
		if (clazz == BigDecimal.class) {
			if (o instanceof Number || o instanceof String) {
				return (T) new BigDecimal(o.toString());
			}
		}

		// String
		if (clazz == String.class) {
			return (T) o.toString();
		}

		// Integer
		if (clazz == Integer.class) {
			return (T) Integer.valueOf(o.toString());
		}

		// Long
		if (clazz == Long.class) {
			return (T) Long.valueOf(o.toString());
		}

		// Double
		if (clazz == Double.class) {
			return (T) Double.valueOf(o.toString());
		}

		// Boolean
		if (clazz == Boolean.class) {
			return (T) Boolean.valueOf(o.toString());
		}

		throw new IllegalArgumentException(
				"Unsupported conversion from " + o.getClass() + " to " + clazz
		);
	}

	protected LocalDateTime now() {
		return LocalDateTime.now();
	}

	private static final ObjectMapper sharedMapper = new ObjectMapper()
			.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
			.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public static <T> String toJson(final T any, final String logExc) {
		try {
			return sharedMapper.writeValueAsString(any);
		} catch (JsonProcessingException e) {
			String message = "Convert " + any.getClass() + "to JSON failed \n" + logExc;
			throw new RuntimeException(message, e);
		}
	}
}
