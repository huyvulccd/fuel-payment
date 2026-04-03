package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.FuelInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng fuel_inventory.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface FuelInventoryRepository extends JpaRepository<FuelInventory, Long> {

    /**
     * Tìm danh sách tồn kho nhiên liệu theo trạm xăng.
     *
     * @param stationId ID trạm xăng
     * @return danh sách tồn kho
     */
    List<FuelInventory> findByStationId(Long stationId);
}
