package idea.fuel_payment.order_service.domain.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique order codes in format: ORD + yyyyMMdd + 6-digit sequence.
 * Example: ORD20260404000001
 *
 * @author order-service
 * @version 2026/04/04
 */
public final class OrderCodeGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    private OrderCodeGenerator() {
    }

    /**
     * Generate a unique order code.
     *
     * @return order code string
     */
    public static String generate() {
        String date = LocalDate.now().format(DATE_FMT);
        long seq = SEQUENCE.incrementAndGet();
        return String.format("ORD%s%06d", date, seq % 1_000_000);
    }
}
