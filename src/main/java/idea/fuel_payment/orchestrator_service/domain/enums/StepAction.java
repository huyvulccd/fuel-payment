package idea.fuel_payment.orchestrator_service.domain.enums;

public enum StepAction {
	CREATE_ORDER,
	CHECK_BALANCE,
	ACTIVATE_PUMP,
	WAIT_PUMP_COMPLETE,
	PROCESS_PAYMENT,
	UPDATE_INVENTORY,
	COMPLETE_ORDER,
	SEND_NOTIFICATION,

	// Compensation actions
	CANCEL_ORDER,
	REFUND_PAYMENT,
	DEACTIVATE_PUMP,
	ROLLBACK_INVENTORY
}
