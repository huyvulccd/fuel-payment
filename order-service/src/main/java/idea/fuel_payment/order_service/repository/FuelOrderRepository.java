package idea.fuel_payment.order_service.repository;

import idea.fuel_payment.order_service.domain.enity.FuelOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for fuel_order table.
 *
 * @author order-service
 * @version 2026/04/04
 */
@Repository
public interface FuelOrderRepository extends JpaRepository<FuelOrder, Long> {

    /**
     * Find a fuel order by its unique order code.
     *
     * @param orderCode the order code
     * @return the fuel order if found
     */
    Optional<FuelOrder> findByOrderCode(String orderCode);

    /**
     * Find all fuel orders belonging to a specific owner.
     *
     * @param ownerId the owner ID
     * @return list of fuel orders
     */
    List<FuelOrder> findByVehicle_Owner_IdOrderByCreatedAtDesc(Long ownerId);
}
