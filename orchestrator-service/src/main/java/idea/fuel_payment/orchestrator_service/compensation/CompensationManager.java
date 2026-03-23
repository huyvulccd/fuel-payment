package idea.fuel_payment.orchestrator_service.compensation;

import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CompensationManager {

	private final Map<StepName, CompensationStrategy> strategies = new ConcurrentHashMap<>();

	public void register(StepName stepName, CompensationStrategy strategy) {
		strategies.put(stepName, strategy);
	}

	public void compensate(String sagaId, StepName stepName, String contextPayloadJson) {
		CompensationStrategy strategy = strategies.get(stepName);
		if (strategy != null) {
			strategy.compensate(sagaId, stepName, contextPayloadJson);
		}
	}
}
