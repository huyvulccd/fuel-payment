package idea.fuel_payment.order_service.kafka.dto;

public enum StepStatus {
	PENDING,
	IN_PROGRESS,
	SUCCESS,
	FAILED,
	COMPENSATING,
	COMPENSATED,
	SKIPPED
}
