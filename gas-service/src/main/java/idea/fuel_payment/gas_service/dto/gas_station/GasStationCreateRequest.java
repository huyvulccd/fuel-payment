package idea.fuel_payment.gas_service.dto.gas_station;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * New gas station creation request.
 *
 * @param stationCode station code (unique)
 * @param stationName station name
 * @author gas-service
 * @version 2026/04/04
 */
public record GasStationCreateRequest(
        @NotBlank(message = "stationCode is required")
        @Size(max = 20, message = "stationCode must be at most 20 characters")
        String stationCode,

        @NotBlank(message = "stationName is required")
        @Size(max = 100, message = "stationName must be at most 100 characters")
        String stationName
) {
}
