package idea.fuel_payment.gas_service.dto.fuel_pump;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Request to create one or more fuel pumps for a gas station.
 *
 * @param stationId  gas station ID
 * @param pumps      list of fuel pumps to create
 * @author gas-service
 * @version 2026/04/04
 */
public record FuelPumpCreateRequest(
        @NotNull(message = "stationId is required")
        Long stationId,

        @NotEmpty(message = "pumps list must not be empty")
        @Valid
        List<PumpItem> pumps
) {

    /**
     * Information of a fuel pump to create.
     *
     * @param pumpNumber pump number
     * @param fuelType   fuel type
     */
    public record PumpItem(
            @NotNull(message = "pumpNumber is required")
            @Positive(message = "pumpNumber must be positive")
            Integer pumpNumber,

            @NotNull(message = "fuelType is required")
            FuelType fuelType
    ) {
    }
}
