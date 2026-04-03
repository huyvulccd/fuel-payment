package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryResponse;
import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryUpdateRequest;
import idea.fuel_payment.gas_service.service.FuelInventoryService;
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
 * Controller quản lý tồn kho nhiên liệu.
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
     * Lấy danh sách tồn kho nhiên liệu theo trạm xăng.
     *
     * @param stationId ID trạm xăng
     * @return danh sách tồn kho
     */
    @GetMapping("/{stationId}")
    public ResponseEntity<List<FuelInventoryResponse>> getInventoryByStationId(
            @PathVariable final Long stationId) {
        return ResponseEntity.ok(fuelInventoryService.getInventoryByStationId(stationId));
    }

    /**
     * Cập nhật thể tích tồn kho nhiên liệu.
     *
     * @param id ID bản ghi tồn kho
     * @param request thông tin cập nhật
     * @return thông tin tồn kho sau cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<FuelInventoryResponse> updateInventory(
            @PathVariable final Long id,
            @Valid @RequestBody final FuelInventoryUpdateRequest request) {
        return ResponseEntity.ok(fuelInventoryService.updateInventory(id, request));
    }
}
