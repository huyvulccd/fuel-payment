package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.GasStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for gas_stations table.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface GasStationRepository extends JpaRepository<GasStation, Long> {

    /**
     * Find gas station by station code.
     *
     * @param stationCode gas station code
     * @return Optional containing GasStation if found
     */
    Optional<GasStation> findByStationCode(String stationCode);
}
