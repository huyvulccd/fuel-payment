package idea.fuel_payment.orchestrator_service.domain.repository;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaStepLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaStepLogRepository extends JpaRepository<SagaStepLog, Long> {

	List<SagaStepLog> findBySagaIdOrderByStepOrderAsc(String sagaId);
}
