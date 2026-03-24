package idea.fuel_payment.orchestrator_service.timeout;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SagaTimeoutChecker {

	@Scheduled(fixedDelayString = "${app.saga.timeout.check-interval-ms:60000}")
	public void checkTimeouts() {
		// load sagas in PROCESSING and verify against StepTimeoutConfig
	}
}
