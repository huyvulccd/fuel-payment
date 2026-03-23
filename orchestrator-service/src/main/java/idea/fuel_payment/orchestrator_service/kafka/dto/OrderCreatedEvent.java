package idea.fuel_payment.orchestrator_service.kafka.dto;

public record OrderCreatedEvent(
		String orderCode,
		String licensePlate,
		String payloadJson
) {
}
