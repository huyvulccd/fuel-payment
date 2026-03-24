package idea.fuel_payment.orchestrator_service.timeout;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaStepLog;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.saga.timeout")
public class StepTimeoutConfig {

	private Map<SagaStepLog.StepName, Duration> step = new EnumMap<>(SagaStepLog.StepName.class);

	public Map<SagaStepLog.StepName, Duration> getStep() {
		return step;
	}

	public void setStep(Map<SagaStepLog.StepName, Duration> step) {
		this.step = step != null ? new EnumMap<>(step) : new EnumMap<>(SagaStepLog.StepName.class);
	}

	public Duration forStep(SagaStepLog.StepName stepName) {
		return step.getOrDefault(stepName, Duration.ofMinutes(5));
	}
}
