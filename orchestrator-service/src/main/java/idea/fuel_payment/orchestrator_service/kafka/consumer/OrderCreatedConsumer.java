package idea.fuel_payment.orchestrator_service.kafka.consumer;

import idea.fuel_payment.orchestrator_service.engine.SagaEngine;
import idea.fuel_payment.orchestrator_service.kafka.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

	private final SagaEngine sagaEngine;

	@KafkaListener(
			topics = "${kafka.topics.order-created:fuel.order.created}",
			groupId = "${spring.kafka.consumer.group-id}",
			containerFactory = "kafkaListenerContainerFactory"
	)
	public void consume(
			@Payload OrderCreatedEvent event,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.RECEIVED_KEY) String key,
			@Header(KafkaHeaders.OFFSET) long offset,
			Acknowledgment ack) {

		log.info("Received OrderCreated: topic={}, key={}, " +
						"offset={}, orderCode={}",
				topic, key, offset, event.orderCode());

		try {
			sagaEngine.startSaga(event);
			ack.acknowledge();
			log.info("Successfully processed OrderCreated: {}",
					event.orderCode());
		} catch (Exception e) {
			log.error("Failed to process OrderCreated: {}",
					event.orderCode(), e);
			// if not acknowledge → Kafka will re-deliver
			throw e;
		}
	}
}