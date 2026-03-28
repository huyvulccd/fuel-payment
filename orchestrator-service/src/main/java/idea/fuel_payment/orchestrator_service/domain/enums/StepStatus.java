package idea.fuel_payment.orchestrator_service.domain.enums;

public enum StepStatus {
	PENDING,
	IN_PROGRESS,
	SUCCESS,
	FAILED,
	COMPENSATING,
	COMPENSATED,
	SKIPPED
}
