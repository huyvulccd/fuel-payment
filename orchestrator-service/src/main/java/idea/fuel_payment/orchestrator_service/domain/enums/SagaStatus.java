package idea.fuel_payment.orchestrator_service.domain.enums;

public enum SagaStatus {
	STARTED,
	PROCESSING,
	COMPENSATING,
	COMPLETED,
	FAILED
}
