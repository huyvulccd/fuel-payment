package idea.fuel_payment.order_service.service.producer;

import idea.fuel_payment.order_service.dto.kafka.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {
	private final static String TOPIC_CREATE_ORDER = "fuel.order.created";

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void sendOrderCreatedEvent(OrderCreatedEvent event) {

		String key = event.orderCode();

		kafkaTemplate.send(TOPIC_CREATE_ORDER, key, event)
				.whenComplete((result, ex) -> {
					if (ex == null) {
						log.info("Sent OrderCreated: topic={}, key={}, offset={}",
								TOPIC_CREATE_ORDER,
								key,
								result.getRecordMetadata().offset());
					} else {
						log.error("Failed to send OrderCreated: {}",
								event.orderCode(), ex);
					}
				});
	}
}
