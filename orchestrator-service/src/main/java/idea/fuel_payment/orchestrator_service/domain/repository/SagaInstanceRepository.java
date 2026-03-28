package idea.fuel_payment.orchestrator_service.domain.repository;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaInstance;
import idea.fuel_payment.orchestrator_service.domain.enums.SagaStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

	Optional<SagaInstance> findBySagaId(String sagaId);

	Optional<SagaInstance> findByOrderCode(String orderCode);

	List<SagaInstance> findBySagaStatus(SagaStatus status);

	@Query("""
        SELECT s FROM SagaInstance s
        WHERE s.sagaStatus IN ('STARTED', 'IN_PROGRESS')
        AND s.updatedAt < :threshold
    """)
	List<SagaInstance> findTimedOutSagas(
			@Param("threshold") LocalDateTime threshold
	);

	boolean existsByOrderCode(String orderCode);
}
