package idea.fuel_payment.gas_service.dto.fuel_price;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelPriceResponse {

    private Long id;
    private FuelType fuelType;
    private BigDecimal price;
    private LocalDateTime effectiveFrom;
    private Boolean isCurrent;
    private LocalDateTime createdAt;
}
