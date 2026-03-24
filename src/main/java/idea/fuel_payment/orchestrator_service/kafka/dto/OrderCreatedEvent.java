package idea.fuel_payment.orchestrator_service.kafka.dto;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
		String orderCode,
		String licensePlate,
		Long ownerId,
		Long stationId,
		Long pumpId,
		String fuelType,
		LocalDateTime createdAt
) {
}
