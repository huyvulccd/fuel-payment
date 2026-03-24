package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaStatus;

public record SagaCompletedEvent(
		String sagaId,
		String orderCode,
		SagaStatus sagaStatus,
		String payloadJson
) {
}
