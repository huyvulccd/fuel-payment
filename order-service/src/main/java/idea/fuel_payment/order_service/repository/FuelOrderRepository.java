package idea.fuel_payment.order_service.repository;

import idea.fuel_payment.order_service.domain.enity.FuelOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelOrderRepository extends JpaRepository<FuelOrder, Long> {
}
