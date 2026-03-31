package idea.fuel_payment.orchestrator_service.kafka.dto;

import idea.fuel_payment.orchestrator_service.domain.enums.StepAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStepCommand {
	private String sagaId;
	private String orderCode;
	private Integer stepOrder;
	private StepAction action;
	private Map<String, Object> payload;
	private Long timestamp;

	public static SagaStepCommand of(
			String sagaId,
			String orderCode,
			Integer stepOrder,
			StepAction action,
			Map<String, Object> payload) {
		return SagaStepCommand.builder()
				.sagaId(sagaId)
				.orderCode(orderCode)
				.stepOrder(stepOrder)
				.action(action)
				.payload(payload)
				.timestamp(System.currentTimeMillis())
				.build();
	}
}