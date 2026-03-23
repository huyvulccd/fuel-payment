package idea.fuel_payment.gas_service.domain.dto_protected;


import idea.fuel_payment.gas_service.domain.entity.FuelPrice;

import java.math.BigDecimal;

public interface FuelPriceDto {
	FuelPrice.FuelType getFuelType();
	BigDecimal getPrice();
}
