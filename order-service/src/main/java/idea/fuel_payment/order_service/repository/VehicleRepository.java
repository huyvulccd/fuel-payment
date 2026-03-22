package idea.fuel_payment.order_service.repository;

import idea.fuel_payment.order_service.domain.enity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	Optional<Vehicle> findOneByLicensePlate(String licensePlate);
	java.util.List<Vehicle> findByOwnerId(Long ownerId);
	boolean existsByLicensePlate(String licensePlate);
}
