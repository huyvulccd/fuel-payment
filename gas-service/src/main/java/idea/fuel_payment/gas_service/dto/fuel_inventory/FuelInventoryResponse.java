package idea.fuel_payment.gas_service.dto.fuel_inventory;

import idea.fuel_payment.gas_service.domain.enums.FuelType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response thông tin tồn kho nhiên liệu.
 *
 * @param id ID bản ghi
 * @param stationId ID trạm xăng
 * @param fuelType loại nhiên liệu
 * @param currentVolume thể tích hiện tại (lít)
 * @param updatedAt thời gian cập nhật
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
