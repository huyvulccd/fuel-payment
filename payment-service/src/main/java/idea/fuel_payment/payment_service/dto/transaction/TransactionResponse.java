package idea.fuel_payment.payment_service.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response cho thông tin giao dịch.
 *
 * @param id ID giao dịch
 * @param transactionCode mã giao dịch
 * @param ownerId ID chủ sở hữu
 * @param kind loại giao dịch
 * @param status trạng thái
 * @param balanceBefore số dư trước
 * @param balanceAfter số dư sau
 * @param failureReason lý do thất bại
 * @param createdAt thời gian tạo
 * @param updatedAt thời gian cập nhật
 * @author payment-service
 * @version 2026/04/01
 */
public record TransactionResponse(
        Long id,
        String transactionCode,
        Long ownerId,
        String kind,
        String status,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
