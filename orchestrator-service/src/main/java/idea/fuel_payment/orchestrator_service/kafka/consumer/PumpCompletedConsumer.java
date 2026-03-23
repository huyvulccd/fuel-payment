package idea.fuel_payment.orchestrator_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PumpCompletedConsumer {

	@KafkaListener(topics = "${app.kafka.topics.pump-completed}")
	public void onPumpCompleted(String payload) {
		// deserialize and resume WAIT_PUMP_COMPLETE step
	}
}
