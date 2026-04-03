package idea.fuel_payment.gas_service.dto.fuel_pump;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.domain.enums.PumpStatus;

import java.time.LocalDateTime;

/**
 * Response thông tin trụ bơm.
 *
 * @param id ID trụ bơm
 * @param stationId ID trạm xăng
 * @param pumpNumber số hiệu trụ bơm
 * @param fuelType loại nhiên liệu
 * @param pumpStatus trạng thái trụ bơm
 * @param createdAt thời gian tạo
 * @param updatedAt thời gian cập nhật
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
