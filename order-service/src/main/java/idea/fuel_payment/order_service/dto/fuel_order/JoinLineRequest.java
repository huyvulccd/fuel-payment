package idea.fuel_payment.order_service.dto.fuel_order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request to join the fueling line at a gas station.
 *
 * @param licensePlate vehicle license plate
 * @param stationId    gas station ID
 * @author order-service
 * @version 2026/04/04
 */
public record JoinLineRequest(
        @NotBlank(message = "licensePlate is required")
        String licensePlate,

        @NotNull(message = "stationId is required")
        Long stationId
) {
}
