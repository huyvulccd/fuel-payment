package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.GasStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho bảng gas_stations.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface GasStationRepository extends JpaRepository<GasStation, Long> {

    /**
     * Tìm trạm xăng theo mã trạm.
     *
     * @param stationCode mã trạm xăng
     * @return Optional chứa GasStation nếu tìm thấy
     */
    Optional<GasStation> findByStationCode(String stationCode);
}
