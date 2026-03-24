package idea.fuel_payment.orchestrator_service.kafka.dto;

public record PumpCompletedEvent(
		String orderCode,
		String pumpId,
		String payloadJson
) {
}
