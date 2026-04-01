package idea.fuel_payment.payment_service.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Request đăng ký thanh toán đơn hàng.
 *
 * @param ownerId ID chủ sở hữu
 * @param orderCode mã đơn hàng
 * @param licensePlate biển số xe
 * @param amount số tiền thanh toán
 * @author payment-service
 * @version 2026/04/01
 */
public record PaymentRegisterRequest(
        @NotNull(message = "ownerId is required")
        Long ownerId,

        @NotBlank(message = "orderCode is required")
        String orderCode,

        @NotBlank(message = "licensePlate is required")
        String licensePlate,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be positive")
        BigDecimal amount
) {
}
