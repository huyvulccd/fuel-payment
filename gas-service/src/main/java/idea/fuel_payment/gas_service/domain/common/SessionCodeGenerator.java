package idea.fuel_payment.gas_service.domain.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility sinh mã phiên bơm xăng duy nhất.
 *
 * @author gas-service
 * @version 2026/04/03
 */
public final class SessionCodeGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private SessionCodeGenerator() {
        // Prevent instantiation
    }

    /**
     * Sinh mã phiên bơm dạng SES-{yyyyMMddHHmmss}-{randomHex8}.
     *
     * @return mã phiên bơm duy nhất
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "SES-" + timestamp + "-" + randomPart;
    }
}
