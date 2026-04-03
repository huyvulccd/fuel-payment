package idea.fuel_payment.gas_service.dto.fuel_inventory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request cập nhật tồn kho nhiên liệu.
 *
 * @param currentVolume thể tích hiện tại (lít)
 * @author gas-service
 * @version 2026/04/03
 */
public record FuelInventoryUpdateRequest(
        @NotNull(message = "currentVolume is required")
        @DecimalMin(value = "0.0", message = "currentVolume must not be negative")
        BigDecimal currentVolume
) {
}
