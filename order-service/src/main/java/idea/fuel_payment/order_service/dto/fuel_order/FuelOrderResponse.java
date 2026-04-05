package idea.fuel_payment.order_service.dto.fuel_order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Full fuel order response.
 *
 * @param orderCode      unique order code
 * @param licensePlate   vehicle license plate
 * @param stationId      gas station ID
 * @param pumpId         assigned pump ID
 * @param fuelType       fuel type
 * @param unitPrice      price per liter
 * @param quantityLiters liters pumped
 * @param totalAmount    total cost
 * @param status         order status
 * @param createdAt      creation timestamp
 * @param updatedAt      last update timestamp
 * @author order-service
 * @version 2026/04/04
 */
public record FuelOrderResponse(
        String orderCode,
        String licensePlate,
        Long stationId,
        Long pumpId,
        String fuelType,
        BigDecimal unitPrice,
        BigDecimal quantityLiters,
        BigDecimal totalAmount,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
