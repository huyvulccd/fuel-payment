package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.entity.FuelInventory;
import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryAddRequest;
import idea.fuel_payment.gas_service.dto.fuel_inventory.FuelInventoryResponse;
import idea.fuel_payment.gas_service.repository.FuelInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service for handling fuel inventory business logic.
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
     * Get list of fuel inventory by gas station.
     *
     * @param stationId gas station ID
     * @return list of inventory
     */
    @Transactional(readOnly = true)
    public List<FuelInventoryResponse> getInventoryByStationId(final Long stationId) {
        return fuelInventoryRepository.findByStationId(stationId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Add fuel inventory volume.
     * Finds the inventory record by station and fuel type, and increments the volume.
     * If the record does not exist, a new one is created.
     *
     * @param request addition information
     * @return inventory information after addition
     */
    @Transactional
    public FuelInventoryResponse addFuelToInventory(final FuelInventoryAddRequest request) {
        FuelInventory inventory = fuelInventoryRepository.findByStationIdAndFuelType(
                        request.stationId(), request.fuelType())
                .orElseGet(() -> FuelInventory.builder()
                        .stationId(request.stationId())
                        .fuelType(request.fuelType())
                        .currentVolume(BigDecimal.ZERO)
                        .build());

        BigDecimal oldVolume = inventory.getCurrentVolume();
        BigDecimal newVolume = oldVolume.add(request.addedVolume());
        inventory.setCurrentVolume(newVolume);
        
        FuelInventory saved = fuelInventoryRepository.save(inventory);
        log.info("Added fuel inventory station={} type={} old={} added={} current={}", 
                request.stationId(), request.fuelType(), oldVolume, request.addedVolume(), newVolume);

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
