package idea.fuel_payment.gas_service.dto.fuel_price;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FuelPricesUpdateRequest {
    private LocalDateTime effectiveFrom;

    private List<FuelPriceUpdateRequest> prices;
}
