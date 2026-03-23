package idea.fuel_payment.orchestrator_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

	@KafkaListener(topics = "${app.kafka.topics.order-created}")
	public void onOrderCreated(String payload) {
		// deserialize and start saga
	}
}
