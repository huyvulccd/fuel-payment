package idea.fuel_payment.orchestrator_service.saga;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaType;
import idea.fuel_payment.orchestrator_service.domain.enums.StepAction;
import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import idea.fuel_payment.orchestrator_service.engine.SagaDefinition;
import idea.fuel_payment.orchestrator_service.engine.SagaStepDefinition;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FuelPurchaseSagaDefinition implements SagaDefinition {
	String COMMAND_STEP = "fuel.saga.step.command";

	private final List<SagaStepDefinition> steps;

	public FuelPurchaseSagaDefinition() {
		this.steps = initializeSteps();
	}

	private List<SagaStepDefinition> initializeSteps() {
		return List.of(
				SagaStepDefinition.builder()
						.stepOrder(1)
						.stepName(StepName.CREATE_ORDER)
						.forwardAction(StepAction.CREATE_ORDER)
						.compensationAction(StepAction.CANCEL_ORDER)
						.targetTopic(COMMAND_STEP)
						.timeoutSeconds(30)
						.compensable(true)
						.asyncWait(false)
						.build(),

				SagaStepDefinition.builder()
						.stepOrder(2)
						.stepName(StepName.CHECK_BALANCE)
						.forwardAction(StepAction.CHECK_BALANCE)
						.compensationAction(null)   // Không cần compensate
						.targetTopic(COMMAND_STEP)
						.timeoutSeconds(30)
						.compensable(false)
						.asyncWait(false)
						.build(),

				SagaStepDefinition.builder()
						.stepOrder(3)
						.stepName(StepName.ACTIVATE_PUMP)
						.forwardAction(StepAction.ACTIVATE_PUMP)
						.compensationAction(StepAction.DEACTIVATE_PUMP)
						.targetTopic(COMMAND_STEP)
						.timeoutSeconds(60)
						.compensable(true)
						.asyncWait(false)
						.build(),

				SagaStepDefinition.builder()
						.stepOrder(4)
						.stepName(StepName.WAIT_PUMP_COMPLETE)
						.forwardAction(StepAction.WAIT_PUMP_COMPLETE)
						.compensationAction(StepAction.DEACTIVATE_PUMP)
						.targetTopic(COMMAND_STEP)
						.timeoutSeconds(600)        // 10 phút chờ bơm
						.compensable(true)
						.asyncWait(true)            // Chờ external event
						.build(),


				SagaStepDefinition.builder()
						.stepOrder(5)
						.stepName(StepName.PROCESS_PAYMENT)
						.forwardAction(StepAction.PROCESS_PAYMENT)
						.compensationAction(StepAction.DEBIT_SUPPORT)
						.targetTopic(COMMAND_STEP)
						.timeoutSeconds(30)
						.compensable(true)
						.asyncWait(false)
						.build(),

				SagaStepDefinition.builder()
						.stepOrder(6)
						.stepName(StepName.UPDATE_INVENTORY)
						.forwardAction(StepAction.UPDATE_INVENTORY)
						.compensationAction(StepAction.ROLLBACK_INVENTORY)
						.targetTopic(COMMAND_STEP)
						.timeoutSeconds(30)
						.compensable(true)
						.asyncWait(false)
						.build(),

				SagaStepDefinition.builder()
						.stepOrder(7)
						.stepName(StepName.COMPLETE_ORDER)
						.forwardAction(StepAction.COMPLETE_ORDER)
						.compensationAction(StepAction.CANCEL_ORDER)
						.targetTopic(COMMAND_STEP)
						.timeoutSeconds(30)
						.compensable(true)
						.asyncWait(false)
						.build()

//				SagaStepDefinition.builder()
//						.stepOrder(8)
//						.stepName(StepName.SEND_NOTIFICATION)
//						.forwardAction(StepAction.SEND_NOTIFICATION)
//						.compensationAction(null)
//						.targetTopic(COMMAND_STEP)
//						.timeoutSeconds(30)
//						.compensable(false)
//						.asyncWait(false)
//						.build()
		);
	}

	@Override
	public SagaType getType() {
		return SagaType.FUEL_PURCHASE;
	}

	@Override
	public List<SagaStepDefinition> getSteps() {
		return Collections.unmodifiableList(steps);
	}

	@Override
	public int getTotalSteps() {
		return steps.size();
	}

	@Override
	public Optional<SagaStepDefinition> getStep(int stepOrder) {
		return steps.stream()
				.filter(s -> s.stepOrder() == stepOrder)
				.findFirst();
	}

	@Override
	public List<SagaStepDefinition> getCompensationSteps(
			int fromStepOrder) {
		return steps.stream()
				.filter(s -> s.stepOrder() < fromStepOrder)
				.filter(SagaStepDefinition::compensable)
				.sorted(Comparator.comparingInt(
						SagaStepDefinition::stepOrder).reversed()
				)
				.collect(Collectors.toList());
	}
}
