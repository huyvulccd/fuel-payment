package idea.fuel_payment.gas_service.domain.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
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

	public <T> T getValue(T first, T second, T valDef) {
		if (Objects.nonNull(first))
			return first;
		if (Objects.nonNull(second))
			return second;

		if (Objects.isNull(valDef))
			throw new IllegalArgumentException("value default must not null");
		return valDef;
	}

	public static <T> Map<String, Object> toMap(T input) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			if (input instanceof String) {
				return mapper.readValue((String) input,
						new TypeReference<Map<String, Object>>() {});
			}

			return mapper.convertValue(input,
					new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			throw new RuntimeException("Invalid JSON", e);
		}
	}
}
