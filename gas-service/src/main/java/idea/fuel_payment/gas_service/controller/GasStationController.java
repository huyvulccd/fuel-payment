package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.gas_station.GasStationResponse;
import idea.fuel_payment.gas_service.dto.gas_station.GasStationUpdateRequest;
import idea.fuel_payment.gas_service.service.GasStationService;
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
 * Controller quản lý trạm xăng.
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
     * Lấy danh sách tất cả trạm xăng.
     *
     * @return danh sách trạm xăng
     */
    @GetMapping
    public ResponseEntity<List<GasStationResponse>> getAllStations() {
        return ResponseEntity.ok(gasStationService.getAllStations());
    }

    /**
     * Lấy thông tin trạm xăng theo ID.
     *
     * @param id ID trạm xăng
     * @return thông tin trạm xăng
     */
    @GetMapping("/{id}")
    public ResponseEntity<GasStationResponse> getStationById(
            @PathVariable final Long id) {
        return ResponseEntity.ok(gasStationService.getStationById(id));
    }

    /**
     * Cập nhật trạng thái trạm xăng.
     *
     * @param id ID trạm xăng
     * @param request thông tin cập nhật
     * @return thông tin trạm xăng sau cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<GasStationResponse> updateStation(
            @PathVariable final Long id,
            @Valid @RequestBody final GasStationUpdateRequest request) {
        return ResponseEntity.ok(gasStationService.updateStation(id, request));
    }
}
