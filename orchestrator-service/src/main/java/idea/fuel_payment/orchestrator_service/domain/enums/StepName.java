package idea.fuel_payment.orchestrator_service.domain.enums;

public enum StepName {
	CREATE_ORDER,
	CHECK_BALANCE,
	ACTIVATE_PUMP,
	WAIT_PUMP_COMPLETE,
	PROCESS_PAYMENT,
	UPDATE_INVENTORY,
	COMPLETE_ORDER,
	SEND_NOTIFICATION
}
