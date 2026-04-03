package idea.fuel_payment.gas_service.dto.fuel_price;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FuelPriceUpdateRequest {

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

	@NotNull(message = "fuel type is required")
	private FuelType fuelType;

    private LocalDateTime effectiveFrom;
}
