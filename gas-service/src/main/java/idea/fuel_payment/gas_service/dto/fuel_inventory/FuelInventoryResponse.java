package idea.fuel_payment.gas_service.dto.fuel_inventory;

import idea.fuel_payment.gas_service.domain.enums.FuelType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fuel inventory information response.
 *
 * @param id record ID
 * @param stationId gas station ID
 * @param fuelType fuel type
 * @param currentVolume current volume (liters)
 * @param updatedAt update time
 * @author gas-service
 * @version 2026/04/03
 */
public record FuelInventoryResponse(
        Long id,
        Long stationId,
        FuelType fuelType,
        BigDecimal currentVolume,
        LocalDateTime updatedAt
) {
}
