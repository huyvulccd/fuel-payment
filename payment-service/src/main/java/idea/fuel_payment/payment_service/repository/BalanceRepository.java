package idea.fuel_payment.payment_service.repository;

import idea.fuel_payment.payment_service.domain.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByOwnerId(Long ownerId);
}
