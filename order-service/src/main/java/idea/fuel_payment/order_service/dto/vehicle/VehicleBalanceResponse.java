package idea.fuel_payment.order_service.dto.vehicle;

import java.math.BigDecimal;

public record VehicleBalanceResponse(
        String licensePlate,
        BigDecimal balance,
        BigDecimal minBalance,
        BigDecimal availableBalance
) {
}
