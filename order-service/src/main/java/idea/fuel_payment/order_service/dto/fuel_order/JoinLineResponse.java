package idea.fuel_payment.order_service.dto.fuel_order;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response after successfully joining the fueling line.
 *
 * @param orderCode    unique order code
 * @param licensePlate vehicle license plate
 * @param stationId    gas station ID
 * @param fuelPumpId   assigned fuel pump ID
 * @param fuelType     type of fuel
 * @param unitPrice    current fuel unit price
 * @param messages     informational messages
 * @author order-service
 * @version 2026/04/04
 */
public record JoinLineResponse(
        String orderCode,
        String licensePlate,
        Long stationId,
        Long fuelPumpId,
        String fuelType,
        BigDecimal unitPrice,
        List<String> messages
) {
}
