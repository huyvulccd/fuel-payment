package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.entity.FuelInventory;
import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryResponse;
import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryUpdateRequest;
import idea.fuel_payment.gas_service.repository.FuelInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service xử lý nghiệp vụ tồn kho nhiên liệu.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FuelInventoryService {

    private final FuelInventoryRepository fuelInventoryRepository;

    /**
     * Lấy danh sách tồn kho nhiên liệu theo trạm xăng.
     *
     * @param stationId ID trạm xăng
     * @return danh sách tồn kho
     */
    @Transactional(readOnly = true)
    public List<FuelInventoryResponse> getInventoryByStationId(final Long stationId) {
        return fuelInventoryRepository.findByStationId(stationId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Cập nhật thể tích tồn kho nhiên liệu.
     *
     * @param id ID bản ghi tồn kho
     * @param request thông tin cập nhật
     * @return thông tin tồn kho sau cập nhật
     * @throws NoSuchElementException nếu không tìm thấy
     */
    @Transactional
    public FuelInventoryResponse updateInventory(final Long id, final FuelInventoryUpdateRequest request) {
        FuelInventory inventory = fuelInventoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Fuel inventory not found: " + id));

        inventory.setCurrentVolume(request.currentVolume());
        FuelInventory saved = fuelInventoryRepository.save(inventory);
        log.info("Updated fuel inventory id={} volume={}", id, request.currentVolume());

        return mapToResponse(saved);
    }

    private FuelInventoryResponse mapToResponse(final FuelInventory inventory) {
        return new FuelInventoryResponse(
                inventory.getId(),
                inventory.getStationId(),
                inventory.getFuelType(),
                inventory.getCurrentVolume(),
                inventory.getUpdatedAt()
        );
    }
}
