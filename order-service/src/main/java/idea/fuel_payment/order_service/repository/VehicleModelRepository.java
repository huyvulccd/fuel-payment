package idea.fuel_payment.order_service.repository;

import idea.fuel_payment.order_service.domain.enity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
	Optional<VehicleModel> findByName(String name);
}
