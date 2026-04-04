package idea.fuel_payment.gas_service.domain.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility for generating unique pump session codes.
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
     * Generate session code in the format SES-{yyyyMMddHHmmss}-{randomHex8}.
     *
     * @return unique pump session code
     */
    public static String generate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "SES-" + timestamp + "-" + randomPart;
    }
}
