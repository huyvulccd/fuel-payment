package idea.fuel_payment.gas_service.dto.query;


import idea.fuel_payment.gas_service.domain.entity.FuelType;

import java.math.BigDecimal;

public interface FuelPricePro {
	FuelType getFuelType();
	BigDecimal getPrice();
}
