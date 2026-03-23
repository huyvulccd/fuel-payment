package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.domain.dto_protected.FuelPriceDto;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPriceResponse;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPriceUpdateRequest;
import idea.fuel_payment.gas_service.service.FuelPriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<FuelPriceDto>> getAllCurrentPrices() {
        return ResponseEntity.ok(fuelPriceService.getAllCurrentPrices());
    }

    /**
     * PUT /api/v1/fuel-prices/{id}
     * Update fuel prices — deactivate old records, create new records, and update Redis cache.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FuelPriceResponse> updatePrice(
            @PathVariable Long id,
            @Valid @RequestBody FuelPriceUpdateRequest request
    ) {
        return ResponseEntity.ok(fuelPriceService.updatePrice(id, request));
    }
}
