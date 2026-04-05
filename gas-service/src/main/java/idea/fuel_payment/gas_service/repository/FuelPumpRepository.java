package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.FuelPump;
import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.domain.enums.PumpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Find the first available pump at a station for a given fuel type,
     * ordered by pump number (smallest queue assignment).
     *
     * @param stationId  gas station ID
     * @param fuelType   type of fuel
     * @param pumpStatus pump status (AVAILABLE)
     * @return the first available pump, if any
     */
    Optional<FuelPump> findFirstByStationIdAndFuelTypeAndPumpStatusOrderByPumpNumberAsc(
            Long stationId, FuelType fuelType, PumpStatus pumpStatus);

	FuelPump findByPumpId(Long pumpId);
}
