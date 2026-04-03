package idea.fuel_payment.gas_service.dto.gas_station;

import idea.fuel_payment.gas_service.domain.enums.StationStatus;

import java.time.LocalDateTime;

/**
 * Response thông tin trạm xăng.
 *
 * @param id ID trạm
 * @param stationCode mã trạm
 * @param stationName tên trạm
 * @param status trạng thái hoạt động
 * @param createdAt thời gian tạo
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
