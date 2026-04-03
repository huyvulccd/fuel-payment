package idea.fuel_payment.gas_service.dto.pump_session;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request đăng ký phiên bơm xăng mới.
 *
 * @param pumpId ID trụ bơm
 * @param orderCode mã đơn hàng
 * @param licensePlate biển số xe
 * @param fuelType loại nhiên liệu
 * @param quantityLiters số lít bơm
 * @param unitPrice đơn giá
 * @param totalAmount tổng tiền
 * @author gas-service
 * @version 2026/04/03
 */
public record PumpSessionRegisterRequest(
        @NotNull(message = "pumpId is required")
        Long pumpId,

        @NotBlank(message = "orderCode is required")
        String orderCode,

        String licensePlate,

        @NotNull(message = "fuelType is required")
        FuelType fuelType,

        @NotNull(message = "quantityLiters is required")
        @DecimalMin(value = "0.001", message = "quantityLiters must be positive")
        BigDecimal quantityLiters,

        @NotNull(message = "unitPrice is required")
        @DecimalMin(value = "0.01", message = "unitPrice must be positive")
        BigDecimal unitPrice,

        @NotNull(message = "totalAmount is required")
        @DecimalMin(value = "0.01", message = "totalAmount must be positive")
        BigDecimal totalAmount
) {
}
