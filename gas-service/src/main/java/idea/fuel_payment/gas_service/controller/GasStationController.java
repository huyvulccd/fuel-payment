package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.gas_station.GasStationCreateRequest;
import idea.fuel_payment.gas_service.dto.gas_station.GasStationResponse;
import idea.fuel_payment.gas_service.dto.gas_station.GasStationUpdateRequest;
import idea.fuel_payment.gas_service.service.GasStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Controller for managing gas stations.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@RestController
@RequestMapping("/api/v1/gas-stations")
@RequiredArgsConstructor
public class GasStationController {

    private final GasStationService gasStationService;

    /**
     * collect all fuels stations.
     *
     * @return list fuels station.
     */
    @GetMapping
    public ResponseEntity<List<GasStationResponse>> getAllStations() {
        return ResponseEntity.ok(gasStationService.getAllStations());
    }

    /**
     * Get gas station information by ID.
     *
     * @param id gas station ID
     * @return gas station information
     */
    @GetMapping("/{id}")
    public ResponseEntity<GasStationResponse> getStationById(
            @PathVariable final Long id) {
        return ResponseEntity.ok(gasStationService.getStationById(id));
    }

    /**
     * Update gas station status.
     *
     * @param id gas station ID
     * @param request update information
     * @return gas station information after update
     */
    @PutMapping("/{id}")
    public ResponseEntity<GasStationResponse> updateStation(
            @PathVariable final Long id,
            @Valid @RequestBody final GasStationUpdateRequest request) {
        return ResponseEntity.ok(gasStationService.updateStation(id, request));
    }

    /**
     * Create new gas station.
     *
     * @param request new gas station information
     * @return newly created gas station information
     */
    @PostMapping
    public ResponseEntity<GasStationResponse> createStation(
            @Valid @RequestBody final GasStationCreateRequest request) {
        GasStationResponse created = gasStationService.createStation(request);
        URI location = URI.create("/api/v1/gas-stations/" + created.id());
        return ResponseEntity.created(location).body(created);
    }
}
