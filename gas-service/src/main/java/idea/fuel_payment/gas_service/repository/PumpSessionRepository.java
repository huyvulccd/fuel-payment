package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.PumpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for pump_sessions table.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface PumpSessionRepository extends JpaRepository<PumpSession, Long> {

    /**
     * Find pump session by order code.
     *
     * @param orderCode order code
     * @return Optional containing PumpSession if found
     */
    Optional<PumpSession> findByOrderCode(String orderCode);
}
