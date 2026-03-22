package idea.fuel_payment.order_service.domain;

import java.util.NoSuchElementException;
import java.util.Optional;

public interface Tools {

	static <T> T getValue(Optional<T> optional, String message) {
		return optional.orElseThrow(() -> new NoSuchElementException(message));
	}
}
