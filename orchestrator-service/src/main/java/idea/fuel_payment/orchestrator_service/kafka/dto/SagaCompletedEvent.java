package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SagaCompletedEvent {

	private String sagaId;
	private String orderCode;
	private SagaStatus finalStatus;
	private Long timestamp;
}