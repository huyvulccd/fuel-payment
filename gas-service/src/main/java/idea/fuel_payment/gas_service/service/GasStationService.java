package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.entity.GasStation;
import idea.fuel_payment.gas_service.dto.gas_station.GasStationCreateRequest;
import idea.fuel_payment.gas_service.dto.gas_station.GasStationResponse;
import idea.fuel_payment.gas_service.dto.gas_station.GasStationUpdateRequest;
import idea.fuel_payment.gas_service.repository.GasStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service for handling gas station business logic.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GasStationService {

    private final GasStationRepository gasStationRepository;

    /**
     * Get list of all gas stations.
     *
     * @return list of gas stations
     */
    @Transactional(readOnly = true)
    public List<GasStationResponse> getAllStations() {
        return gasStationRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get gas station information by ID.
     *
     * @param id gas station ID
     * @return gas station information
     * @throws NoSuchElementException if not found
     */
    @Transactional(readOnly = true)
    public GasStationResponse getStationById(final Long id) {
        GasStation station = gasStationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Gas station not found: " + id));
        return mapToResponse(station);
    }

    /**
     * Update gas station status.
     *
     * @param id gas station ID
     * @param request update information
     * @return gas station information after update
     * @throws NoSuchElementException if not found
     */
    @Transactional
    public GasStationResponse updateStation(final Long id, final GasStationUpdateRequest request) {
        GasStation station = gasStationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Gas station not found: " + id));

        station.setStatus(request.status());
        GasStation saved = gasStationRepository.save(station);
        log.info("Updated gas station id={} status={}", id, request.status());

        return mapToResponse(saved);
    }

    /**
     * Create new gas station.
     *
     * @param request new gas station information
     * @return newly created gas station information
     * @throws IllegalArgumentException if station code already exists
     */
    @Transactional
    public GasStationResponse createStation(final GasStationCreateRequest request) {
        gasStationRepository.findByStationCode(request.stationCode())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Station code already exists: " + request.stationCode());
                });

        GasStation station = GasStation.builder()
                .stationCode(request.stationCode())
                .stationName(request.stationName())
                .build();

        GasStation saved = gasStationRepository.save(station);
        log.info("Created gas station code={} name={}",
                saved.getStationCode(), saved.getStationName());

        return mapToResponse(saved);
    }

    private GasStationResponse mapToResponse(final GasStation station) {
        return new GasStationResponse(
                station.getId(),
                station.getStationCode(),
                station.getStationName(),
                station.getStatus(),
                station.getCreatedAt()
        );
    }
}
