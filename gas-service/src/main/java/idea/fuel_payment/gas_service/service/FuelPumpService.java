package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.entity.FuelPump;
import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.domain.enums.PumpStatus;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpCreateRequest;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpResponse;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpUpdateRequest;
import idea.fuel_payment.gas_service.repository.FuelPumpRepository;
import idea.fuel_payment.gas_service.repository.GasStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service for handling fuel pump business logic.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FuelPumpService {

    private final FuelPumpRepository fuelPumpRepository;
    private final GasStationRepository gasStationRepository;

    /**
     * Get list of fuel pumps by gas station.
     *
     * @param stationId gas station ID
     * @return list of fuel pumps
     */
    @Transactional(readOnly = true)
    public List<FuelPumpResponse> getPumpsByStationId(final Long stationId) {
        return fuelPumpRepository.findByStationId(stationId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Find the first available pump at a station for a given fuel type.
     * Returns the pump with the smallest pump number.
     *
     * @param stationId gas station ID
     * @param fuelType  type of fuel
     * @return available fuel pump
     * @throws NoSuchElementException if no available pump found
     */
    @Transactional(readOnly = true)
    public FuelPumpResponse findAvailablePump(final Long stationId, final FuelType fuelType) {
        FuelPump pump = fuelPumpRepository
                .findFirstByStationIdAndFuelTypeAndPumpStatusOrderByPumpNumberAsc(
                        stationId, fuelType, PumpStatus.AVAILABLE)
                .orElseThrow(() -> new NoSuchElementException(
                        "No available pump at station " + stationId + " for fuel type " + fuelType));

        log.info("Found available pump id={} number={} at station={} for fuelType={}",
                pump.getId(), pump.getPumpNumber(), stationId, fuelType);
        return mapToResponse(pump);
    }

    /**
     * Update fuel pump status.
     *
     * @param id fuel pump ID
     * @param request update information
     * @return fuel pump information after update
     * @throws NoSuchElementException if not found
     */
    @Transactional
    public FuelPumpResponse updatePump(final Long id, final FuelPumpUpdateRequest request) {
        FuelPump pump = fuelPumpRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Fuel pump not found: " + id));

        pump.setPumpStatus(request.pumpStatus());
        FuelPump saved = fuelPumpRepository.save(pump);
        log.info("Updated fuel pump id={} status={}", id, request.pumpStatus());

        return mapToResponse(saved);
    }

    /**
     * Create one or many fuel pumps for a gas station.
     *
     * @param request information of fuel pumps to create
     * @return list of newly created fuel pumps
     * @throws java.util.NoSuchElementException if gas station does not exist
     */
    @Transactional
    public List<FuelPumpResponse> createPumps(final FuelPumpCreateRequest request) {
        gasStationRepository.findById(request.stationId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Gas station not found: " + request.stationId()));

        List<FuelPump> pumps = request.pumps().stream()
                .map(item -> FuelPump.builder()
                        .stationId(request.stationId())
                        .pumpNumber(item.pumpNumber())
                        .fuelType(item.fuelType())
                        .build())
                .toList();

        List<FuelPump> savedPumps = fuelPumpRepository.saveAll(pumps);
        log.info("Created {} fuel pumps for station id={}",
                savedPumps.size(), request.stationId());

        return savedPumps.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Activate a pump (set to IN_USE).
     *
     * @param pumpId fuel pump ID
     * @return updated pump info
     * @throws NoSuchElementException if not found
     */
    @Transactional
    public FuelPumpResponse activatePump(final Long pumpId) {
        FuelPump pump = fuelPumpRepository.findById(pumpId)
                .orElseThrow(() -> new NoSuchElementException("Fuel pump not found: " + pumpId));
        pump.setPumpStatus(PumpStatus.IN_USE);
        FuelPump saved = fuelPumpRepository.save(pump);
        log.info("Activated pump id={} status=IN_USE", pumpId);
        return mapToResponse(saved);
    }

    /**
     * Deactivate a pump (set back to AVAILABLE).
     *
     * @param pumpId fuel pump ID
     * @return updated pump info
     * @throws NoSuchElementException if not found
     */
    @Transactional
    public FuelPumpResponse deactivatePump(final Long pumpId) {
        FuelPump pump = fuelPumpRepository.findById(pumpId)
                .orElseThrow(() -> new NoSuchElementException("Fuel pump not found: " + pumpId));
        pump.setPumpStatus(PumpStatus.AVAILABLE);
        FuelPump saved = fuelPumpRepository.save(pump);
        log.info("Deactivated pump id={} status=AVAILABLE", pumpId);
        return mapToResponse(saved);
    }

    private FuelPumpResponse mapToResponse(final FuelPump pump) {
        return new FuelPumpResponse(
                pump.getId(),
                pump.getStationId(),
                pump.getPumpNumber(),
                pump.getFuelType(),
                pump.getPumpStatus(),
                pump.getCreatedAt(),
                pump.getUpdatedAt()
        );
    }
}
