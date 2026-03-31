package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.StepStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStepResponse {
	private String sagaId;
	private String orderCode;
	private Integer stepOrder;
	private StepStatus status;
	private Map<String, Object> payload;
	private String errorMessage;
	private Long timestamp;
}