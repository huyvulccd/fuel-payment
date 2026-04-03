package idea.fuel_payment.gas_service.dto.query;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuelPriceDto {
	private FuelType fuelType;
	private BigDecimal price;
}
