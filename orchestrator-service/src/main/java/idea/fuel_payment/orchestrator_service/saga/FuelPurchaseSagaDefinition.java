package idea.fuel_payment.orchestrator_service.saga;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaType;
import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import idea.fuel_payment.orchestrator_service.engine.SagaDefinition;
import idea.fuel_payment.orchestrator_service.engine.SagaStepDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FuelPurchaseSagaDefinition implements SagaDefinition {

	@Override
	public SagaType getSagaType() {
		return SagaType.FUEL_PURCHASE;
	}

	@Override
	public List<SagaStepDefinition> getSteps() {
		return List.of(
				new SagaStepDefinition(0, StepName.CREATE_ORDER, "order-service"),
				new SagaStepDefinition(1, StepName.CHECK_BALANCE, "wallet-service"),
				new SagaStepDefinition(2, StepName.ACTIVATE_PUMP, "pump-service"),
				new SagaStepDefinition(3, StepName.WAIT_PUMP_COMPLETE, "pump-service"),
				new SagaStepDefinition(4, StepName.PROCESS_PAYMENT, "payment-service"),
				new SagaStepDefinition(5, StepName.UPDATE_INVENTORY, "inventory-service"),
				new SagaStepDefinition(6, StepName.COMPLETE_ORDER, "order-service"),
				new SagaStepDefinition(7, StepName.SEND_NOTIFICATION, "notification-service")
		);
	}
}
