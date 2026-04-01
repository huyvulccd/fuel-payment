package idea.fuel_payment.payment_service.dto.payment;

import java.math.BigDecimal;

/**
 * Response sau khi đăng ký thanh toán đơn hàng.
 *
 * @param transactionCode mã giao dịch
 * @param ownerId ID chủ sở hữu
 * @param orderCode mã đơn hàng
 * @param licensePlate biển số xe
 * @param amount số tiền thanh toán
 * @param status trạng thái
 * @param balanceBefore số dư trước
 * @param balanceAfter số dư sau
 * @author payment-service
 * @version 2026/04/01
 */
public record PaymentRegisterResponse(
        String transactionCode,
        Long ownerId,
        String orderCode,
        String licensePlate,
        BigDecimal amount,
        String status,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter
) {
}
