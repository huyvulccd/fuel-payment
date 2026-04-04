package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.FuelInventory;
import idea.fuel_payment.gas_service.domain.enums.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for fuel_inventory table.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface FuelInventoryRepository extends JpaRepository<FuelInventory, Long> {

    /**
     * Find list of fuel inventory by gas station.
     *
     * @param stationId gas station ID
     * @return list of inventory
     */
    List<FuelInventory> findByStationId(Long stationId);

    /**
     * Find specific fuel inventory by station and type.
     *
     * @param stationId gas station ID
     * @param fuelType fuel type
     * @return Optional containing the inventory
     */
    java.util.Optional<FuelInventory> findByStationIdAndFuelType(Long stationId, FuelType fuelType);
}
