package idea.fuel_payment.orchestrator_service.kafka.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderCreatedEvent(String orderCode, String licensePlate, Long ownerId, Long stationId, Long pumpId,
                                String fuelType, LocalDateTime createdAt) {

}
