package idea.fuel_payment.order_service.repository;

import idea.fuel_payment.order_service.domain.enity.TotalEnergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalEnergyRepository extends JpaRepository<TotalEnergy, Long> {
    Optional<TotalEnergy> findByOwnerId(Long ownerId);
}
