package idea.fuel_payment.orchestrator_service.domain.repository;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaStepLog;
import idea.fuel_payment.orchestrator_service.domain.enums.StepStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SagaStepLogRepository extends JpaRepository<SagaStepLog, Long> {
	List<SagaStepLog> findBySagaIdOrderByStepOrderAsc(String sagaId);

	Optional<SagaStepLog> findBySagaIdAndStepOrder(
			String sagaId, Integer stepOrder
	);

	@Query("""
        SELECT s FROM saga_step_log s
        WHERE s.sagaId = :sagaId
        AND s.stepStatus = :status
        ORDER BY s.stepOrder DESC
    """)
	List<SagaStepLog> findBySagaIdAndStatus(
			@Param("sagaId") String sagaId,
			@Param("status") StepStatus status
	);
}
