package idea.fuel_payment.payment_service.dto.transfer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Request đăng ký chuyển tiền.
 *
 * @param senderId ID người gửi
 * @param receiverId ID người nhận
 * @param amount số tiền chuyển
 * @param message lời nhắn (tuỳ chọn)
 * @author payment-service
 * @version 2026/04/01
 */
public record TransferRegisterRequest(
        @NotNull(message = "senderId is required")
        Long senderId,

        @NotNull(message = "receiverId is required")
        Long receiverId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be positive")
        BigDecimal amount,

        String message
) {
}
