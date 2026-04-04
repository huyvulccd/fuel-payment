package idea.fuel_payment.gas_service.dto.gas_station;

import idea.fuel_payment.gas_service.domain.enums.StationStatus;

import java.time.LocalDateTime;

/**
 * Gas station information response.
 *
 * @param id station ID
 * @param stationCode station code
 * @param stationName station name
 * @param status operating status
 * @param createdAt creation time
 * @author gas-service
 * @version 2026/04/03
 */
public record GasStationResponse(
        Long id,
        String stationCode,
        String stationName,
        StationStatus status,
        LocalDateTime createdAt
) {
}
