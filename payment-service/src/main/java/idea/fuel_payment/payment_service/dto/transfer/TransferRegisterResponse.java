package idea.fuel_payment.payment_service.dto.transfer;

import java.math.BigDecimal;

/**
 * Response sau khi đăng ký chuyển tiền.
 *
 * @param transactionCode mã giao dịch
 * @param senderId ID người gửi
 * @param receiverId ID người nhận
 * @param amount số tiền chuyển
 * @param message lời nhắn
 * @param status trạng thái
 * @param senderBalanceBefore số dư người gửi trước
 * @param senderBalanceAfter số dư người gửi sau
 * @param receiverBalanceBefore số dư người nhận trước
 * @param receiverBalanceAfter số dư người nhận sau
 * @author payment-service
 * @version 2026/04/01
 */
public record TransferRegisterResponse(
        String transactionCode,
        Long senderId,
        Long receiverId,
        BigDecimal amount,
        String message,
        String status,
        BigDecimal senderBalanceBefore,
        BigDecimal senderBalanceAfter,
        BigDecimal receiverBalanceBefore,
        BigDecimal receiverBalanceAfter
) {
}
