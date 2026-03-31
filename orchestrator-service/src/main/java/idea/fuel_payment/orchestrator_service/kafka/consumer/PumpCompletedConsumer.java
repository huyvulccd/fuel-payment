package idea.fuel_payment.orchestrator_service.kafka.consumer;

import idea.fuel_payment.orchestrator_service.engine.SagaEngine;
import idea.fuel_payment.orchestrator_service.kafka.dto.PumpCompletedEvent;
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
public class PumpCompletedConsumer {

	private final SagaEngine sagaEngine;

	@KafkaListener(
			topics = "${kafka.topics.pump-completed:" +
					"fuel.pump.completed}",
			groupId = "${spring.kafka.consumer.group-id}",
			containerFactory = "kafkaListenerContainerFactory"
	)
	public void consume(
			@Payload PumpCompletedEvent event,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.OFFSET) long offset,
			Acknowledgment ack) {

		log.info("Received PumpCompleted: order={}, " +
						"quantity={}, amount={}",
				event.getOrderCode(),
				event.getQuantityLiters(),
				event.getTotalAmount());

		try {
			sagaEngine.handlePumpCompleted(event);
			ack.acknowledge();
		} catch (Exception e) {
			log.error("Failed to process PumpCompleted: order={}",
					event.getOrderCode(), e);
			throw e;
		}
	}
}