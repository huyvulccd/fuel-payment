package idea.fuel_payment.payment_service.dto.topup;

import java.math.BigDecimal;

/**
 * Response sau khi đăng ký nạp tiền.
 *
 * @param transactionCode mã giao dịch
 * @param ownerId ID chủ sở hữu
 * @param amount số tiền nạp
 * @param topupMethod phương thức nạp tiền
 * @param status trạng thái
 * @param balanceBefore số dư trước
 * @param balanceAfter số dư sau
 * @author payment-service
 * @version 2026/04/01
 */
public record TopupRegisterResponse(
        String transactionCode,
        Long ownerId,
        BigDecimal amount,
        String topupMethod,
        String status,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter
) {
}
