package idea.fuel_payment.orchestrator_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SagaStepResponseConsumer {

	@KafkaListener(topics = "${app.kafka.topics.saga-step-response}")
	public void onSagaStepResponse(String payload) {
		// deserialize and advance saga
	}
}
