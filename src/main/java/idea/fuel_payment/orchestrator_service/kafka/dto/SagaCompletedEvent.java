package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaStatus;
import lombok.Builder;

@Builder
public record SagaCompletedEvent(
		String sagaId,
		String orderCode,
		SagaStatus finalStatus,
		Long timestamp
) {
}
