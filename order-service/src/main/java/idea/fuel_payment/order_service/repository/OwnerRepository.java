package idea.fuel_payment.order_service.repository;

import idea.fuel_payment.order_service.domain.enity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Optional<Owner> findByIdPersonal(String idPersonal);
    Optional<Owner> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
