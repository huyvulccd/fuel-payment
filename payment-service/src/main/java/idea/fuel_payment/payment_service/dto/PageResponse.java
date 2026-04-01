package idea.fuel_payment.payment_service.dto;

import java.util.List;

/**
 * Generic phân trang response.
 *
 * @param data danh sách kết quả
 * @param total tổng số bản ghi
 * @param page trang hiện tại
 * @param size kích thước trang
 * @param <T> kiểu dữ liệu
 * @author payment-service
 * @version 2026/04/01
 */
public record PageResponse<T>(
        List<T> data,
        long total,
        int page,
        int size
) {
}
