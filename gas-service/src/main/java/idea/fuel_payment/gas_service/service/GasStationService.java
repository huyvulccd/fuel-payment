package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.entity.GasStation;
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
 * Service xử lý nghiệp vụ trạm xăng.
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
     * Lấy danh sách tất cả trạm xăng.
     *
     * @return danh sách trạm xăng
     */
    @Transactional(readOnly = true)
    public List<GasStationResponse> getAllStations() {
        return gasStationRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Lấy thông tin trạm xăng theo ID.
     *
     * @param id ID trạm xăng
     * @return thông tin trạm xăng
     * @throws NoSuchElementException nếu không tìm thấy
     */
    @Transactional(readOnly = true)
    public GasStationResponse getStationById(final Long id) {
        GasStation station = gasStationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Gas station not found: " + id));
        return mapToResponse(station);
    }

    /**
     * Cập nhật trạng thái trạm xăng.
     *
     * @param id ID trạm xăng
     * @param request thông tin cập nhật
     * @return thông tin trạm xăng sau cập nhật
     * @throws NoSuchElementException nếu không tìm thấy
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
