package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpResponse;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpUpdateRequest;
import idea.fuel_payment.gas_service.service.FuelPumpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller quản lý trụ bơm xăng.
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
     * Lấy danh sách trụ bơm theo trạm xăng.
     *
     * @param stationId ID trạm xăng
     * @return danh sách trụ bơm
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<List<FuelPumpResponse>> getPumpsByStationId(
            @PathVariable final Long stationId) {
        return ResponseEntity.ok(fuelPumpService.getPumpsByStationId(stationId));
    }

    /**
     * Cập nhật trạng thái trụ bơm.
     *
     * @param id ID trụ bơm
     * @param request thông tin cập nhật
     * @return thông tin trụ bơm sau cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<FuelPumpResponse> updatePump(
            @PathVariable final Long id,
            @Valid @RequestBody final FuelPumpUpdateRequest request) {
        return ResponseEntity.ok(fuelPumpService.updatePump(id, request));
    }
}
