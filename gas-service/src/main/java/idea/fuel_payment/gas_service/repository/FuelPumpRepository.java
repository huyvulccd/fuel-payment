package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.FuelPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng fuel_pumps.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface FuelPumpRepository extends JpaRepository<FuelPump, Long> {

    /**
     * Tìm danh sách trụ bơm theo trạm xăng.
     *
     * @param stationId ID trạm xăng
     * @return danh sách trụ bơm
     */
    List<FuelPump> findByStationId(Long stationId);
}
