package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.entity.FuelPump;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpResponse;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpUpdateRequest;
import idea.fuel_payment.gas_service.repository.FuelPumpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service xử lý nghiệp vụ trụ bơm xăng.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FuelPumpService {

    private final FuelPumpRepository fuelPumpRepository;

    /**
     * Lấy danh sách trụ bơm theo trạm xăng.
     *
     * @param stationId ID trạm xăng
     * @return danh sách trụ bơm
     */
    @Transactional(readOnly = true)
    public List<FuelPumpResponse> getPumpsByStationId(final Long stationId) {
        return fuelPumpRepository.findByStationId(stationId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Cập nhật trạng thái trụ bơm.
     *
     * @param id ID trụ bơm
     * @param request thông tin cập nhật
     * @return thông tin trụ bơm sau cập nhật
     * @throws NoSuchElementException nếu không tìm thấy
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
