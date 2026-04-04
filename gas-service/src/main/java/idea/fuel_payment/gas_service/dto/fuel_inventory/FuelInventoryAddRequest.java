package idea.fuel_payment.gas_service.dto.fuel_inventory;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Fuel inventory addition request.
 *
 * @param stationId  gas station ID
 * @param fuelType   type of fuel to add
 * @param addedVolume volume to add (liters)
 * @author gas-service
 * @version 2026/04/04
 */
public record FuelInventoryAddRequest(
        @NotNull(message = "stationId is required")
        Long stationId,

        @NotNull(message = "fuelType is required")
        FuelType fuelType,

        @NotNull(message = "addedVolume is required")
        @DecimalMin(value = "0.001", message = "addedVolume must be positive")
        BigDecimal addedVolume
) {
}
