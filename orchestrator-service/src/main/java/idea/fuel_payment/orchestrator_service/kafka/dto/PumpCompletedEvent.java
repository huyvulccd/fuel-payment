package idea.fuel_payment.orchestrator_service.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PumpCompletedEvent {

	private final String sessionCode;
	private final String orderCode;
	private final BigDecimal quantityLiters;
	private final BigDecimal totalAmount;
	private final String fuelType;
	private final Long stationId;
	private final Long pumpId;
}