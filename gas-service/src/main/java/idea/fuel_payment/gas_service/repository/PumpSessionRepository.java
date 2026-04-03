package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.PumpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho bảng pump_sessions.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Repository
public interface PumpSessionRepository extends JpaRepository<PumpSession, Long> {

    /**
     * Tìm phiên bơm theo mã đơn hàng.
     *
     * @param orderCode mã đơn hàng
     * @return Optional chứa PumpSession nếu tìm thấy
     */
    Optional<PumpSession> findByOrderCode(String orderCode);
}
