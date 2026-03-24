package idea.fuel_payment.orchestrator_service.domain.repository;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

	Optional<SagaInstance> findBySagaId(String sagaId);
}
