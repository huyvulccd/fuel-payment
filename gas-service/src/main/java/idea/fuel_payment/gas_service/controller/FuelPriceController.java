package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.fuel_price.FuelPriceResponse;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPricesUpdateRequest;
import idea.fuel_payment.gas_service.service.FuelPriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fuel-prices")
@RequiredArgsConstructor
public class FuelPriceController {

    private final FuelPriceService fuelPriceService;

    /**
     * GET /api/v1/fuel-prices
     * Fetch all current gas prices (is_current = true)
     */
    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getAllCurrentPrices() {
        return ResponseEntity.ok(fuelPriceService.getAllCurrentPrices());
    }

    /**
     * PUT /api/v1/fuel-prices/{id}
     * Update fuel prices — deactivate old records, create new records, and update Redis cache.
     */
    @PutMapping
    public ResponseEntity<List<FuelPriceResponse>> updatePrice(
            @Valid @RequestBody FuelPricesUpdateRequest request
    ) {
        return ResponseEntity.ok(fuelPriceService.updatePrice(request));
    }
}
