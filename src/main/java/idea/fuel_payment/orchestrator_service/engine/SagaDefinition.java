package idea.fuel_payment.orchestrator_service.engine;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaType;

import java.util.List;
import java.util.Optional;

public interface SagaDefinition {

	SagaType getType();

	List<SagaStepDefinition> getSteps();

	int getTotalSteps();

	Optional<SagaStepDefinition> getStep(int stepOrder);

	/**
	 * Lấy danh sách các step cần compensate
	 * (ngược từ step hiện tại về step 1)
	 */
	List<SagaStepDefinition> getCompensationSteps(
			int fromStepOrder
	);
}
