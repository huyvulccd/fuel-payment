package idea.fuel_payment.gas_service.dto.gas_station;

import idea.fuel_payment.gas_service.domain.enums.StationStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request cập nhật trạng thái trạm xăng.
 *
 * @param status trạng thái mới
 * @author gas-service
 * @version 2026/04/03
 */
public record GasStationUpdateRequest(
        @NotNull(message = "status is required")
        StationStatus status
) {
}
