package idea.fuel_payment.gas_service.kafka.dto;

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
}
