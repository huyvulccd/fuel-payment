package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpCreateRequest;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpResponse;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpUpdateRequest;
import idea.fuel_payment.gas_service.service.FuelPumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing fuel pumps.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@RestController
@RequestMapping("/api/v1/fuel-pumps")
@RequiredArgsConstructor
public class FuelPumpController {

    private final FuelPumpService fuelPumpService;

    /**
     * Get list of fuel pumps by gas station.
     *
     * @param stationId gas station ID
     * @return list of fuel pumps
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<List<FuelPumpResponse>> getPumpsByStationId(
            @PathVariable final Long stationId) {
        return ResponseEntity.ok(fuelPumpService.getPumpsByStationId(stationId));
    }

    /**
     * Find the first available pump at a station for a given fuel type.
     *
     * @param stationId gas station ID
     * @param fuelType  type of fuel
     * @return available pump information
     */
    @GetMapping("/available")
    public ResponseEntity<FuelPumpResponse> findAvailablePump(
            @RequestParam("stationId") final Long stationId,
            @RequestParam("fuelType") final FuelType fuelType) {
        return ResponseEntity.ok(fuelPumpService.findAvailablePump(stationId, fuelType));
    }

    /**
     * Update fuel pump status.
     *
     * @param id fuel pump ID
     * @param request update information
     * @return fuel pump information after update
     */
    @PutMapping("/{id}")
    public ResponseEntity<FuelPumpResponse> updatePump(
            @PathVariable final Long id,
            @Valid @RequestBody final FuelPumpUpdateRequest request) {
        return ResponseEntity.ok(fuelPumpService.updatePump(id, request));
    }

    /**
     * Create one or many fuel pumps for a gas station.
     *
     * @param request information of fuel pumps to create
     * @return list of newly created fuel pumps
     */
    @PostMapping
    public ResponseEntity<List<FuelPumpResponse>> createPumps(
            @Valid @RequestBody final FuelPumpCreateRequest request) {
        List<FuelPumpResponse> created = fuelPumpService.createPumps(request);
        return ResponseEntity.status(201).body(created);
    }
}
