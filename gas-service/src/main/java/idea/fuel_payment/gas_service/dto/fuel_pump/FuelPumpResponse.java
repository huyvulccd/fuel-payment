package idea.fuel_payment.gas_service.dto.fuel_pump;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.domain.enums.PumpStatus;

import java.time.LocalDateTime;

/**
 * Fuel pump information response.
 *
 * @param id fuel pump ID
 * @param stationId gas station ID
 * @param pumpNumber pump number
 * @param fuelType fuel type
 * @param pumpStatus pump status
 * @param createdAt creation time
 * @param updatedAt update time
 * @author gas-service
 * @version 2026/04/03
 */
public record FuelPumpResponse(
        Long id,
        Long stationId,
        Integer pumpNumber,
        FuelType fuelType,
        PumpStatus pumpStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
