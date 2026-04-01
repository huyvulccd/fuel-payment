package idea.fuel_payment.payment_service.dto.topup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Request đăng ký nạp tiền.
 *
 * @param ownerId ID chủ sở hữu
 * @param amount số tiền nạp
 * @param topupMethod phương thức nạp tiền (BANK_TRANSFER, MOMO, VNPAY)
 * @author payment-service
 * @version 2026/04/01
 */
public record TopupRegisterRequest(
        @NotNull(message = "ownerId is required")
        Long ownerId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be positive")
        BigDecimal amount,

        @NotNull(message = "topupMethod is required")
        String topupMethod
) {
}
