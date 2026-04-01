package idea.fuel_payment.payment_service.domain.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility sinh mã giao dịch duy nhất.
 *
 * @author payment-service
 * @version 2026/04/01
 */
public final class TransactionCodeGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private TransactionCodeGenerator() {
        // Prevent instantiation
    }

    /**
     * Sinh mã giao dịch dạng TXN-{yyyyMMddHHmmss}-{randomHex8}.
     *
     * @return mã giao dịch duy nhất
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "TXN-" + timestamp + "-" + randomPart;
    }
}
