package idea.fuel_payment.orchestrator_service.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderCreatedEvent {

	private final String orderCode;
	private final String licensePlate;
	private final Long ownerId;
	private final Long stationId;
	private final Long pumpId;
	private final String fuelType;
	private final LocalDateTime createdAt;
}
