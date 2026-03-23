package idea.fuel_payment.gas_service.common;

import java.lang.reflect.Array;
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
}
