package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.FuelPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for fuel_pumps table.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface FuelPumpRepository extends JpaRepository<FuelPump, Long> {

    /**
     * Find list of fuel pumps by gas station.
     *
     * @param stationId gas station ID
     * @return list of fuel pumps
     */
    List<FuelPump> findByStationId(Long stationId);
}
