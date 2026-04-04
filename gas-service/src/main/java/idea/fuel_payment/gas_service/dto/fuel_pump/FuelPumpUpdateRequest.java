package idea.fuel_payment.gas_service.dto.fuel_pump;

import idea.fuel_payment.gas_service.domain.enums.PumpStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Fuel pump status update request.
 *
 * @param pumpStatus new status
 * @author gas-service
 * @version 2026/04/03
 */
public record FuelPumpUpdateRequest(
        @NotNull(message = "pumpStatus is required")
        PumpStatus pumpStatus
) {
}
