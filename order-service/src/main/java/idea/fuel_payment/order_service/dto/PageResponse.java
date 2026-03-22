package idea.fuel_payment.order_service.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        long total,
        int page,
        int size
) {
}
