package idea.fuel_payment.gas_service.dto.gas_station;

import idea.fuel_payment.gas_service.domain.enums.StationStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Gas station status update request.
 *
 * @param status new status
 * @author gas-service
 * @version 2026/04/03
 */
public record GasStationUpdateRequest(
        @NotNull(message = "status is required")
        StationStatus status
) {
}
