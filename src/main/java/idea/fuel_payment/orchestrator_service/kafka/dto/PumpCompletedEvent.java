package idea.fuel_payment.orchestrator_service.kafka.dto;

import java.math.BigDecimal;

public record PumpCompletedEvent(
		String sessionCode,
		String orderCode,
		BigDecimal quantityLiters,
		BigDecimal totalAmount,
		String fuelType,
		Long stationId,
		Long pumpId
) {
}
