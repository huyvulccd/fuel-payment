package idea.fuel_payment.payment_service.repository;

import idea.fuel_payment.payment_service.domain.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository cho bảng payment_history.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
}
