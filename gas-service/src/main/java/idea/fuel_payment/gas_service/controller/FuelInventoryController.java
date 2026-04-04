package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryAddRequest;
import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryResponse;
import idea.fuel_payment.gas_service.service.FuelInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing fuel inventory.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@RestController
@RequestMapping("/api/v1/fuel-inventory")
@RequiredArgsConstructor
public class FuelInventoryController {

    private final FuelInventoryService fuelInventoryService;

    /**
     * Get list of fuel inventory by gas station.
     *
     * @param stationId gas station ID
     * @return list of inventory
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<List<FuelInventoryResponse>> getInventoryByStationId(
            @PathVariable final Long stationId) {
        return ResponseEntity.ok(fuelInventoryService.getInventoryByStationId(stationId));
    }

    /**
     * Refill fuel inventory.
     * Finds the inventory record by station and fuel type, and increments the volume.
     *
     * @param request refill information
     * @return inventory information after refill
     */
    @PostMapping("/refill")
    public ResponseEntity<FuelInventoryResponse> refillInventory(
            @Valid @RequestBody final FuelInventoryAddRequest request) {
        return ResponseEntity.ok(fuelInventoryService.addFuelToInventory(request));
    }
}
