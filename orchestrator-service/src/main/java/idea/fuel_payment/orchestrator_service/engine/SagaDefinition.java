package idea.fuel_payment.orchestrator_service.engine;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaType;

import java.util.List;

public interface SagaDefinition {

	SagaType getSagaType();

	List<SagaStepDefinition> getSteps();
}
