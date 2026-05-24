package idea.fuel_payment.order_service.outbox;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

	private final OutboxEventRepository outboxEventRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	/**
	 * Polling job to automatically relay perfectly strictly PENDING outbox events to Kafka.
	 * Runs every 5 seconds (5000 ms).
	 */
	@Scheduled(fixedDelay = 5000)
	@Transactional
	public void relayOutboxEvents() {
		List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxEvent.OutboxStatus.PENDING);

		for (OutboxEvent event : pendingEvents) {
			try {
				// Publish to Kafka. Use eventType as the target topic name.
				String topic = event.getEventType();
				kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());

				// Mark as published
				event.setStatus(OutboxEvent.OutboxStatus.PUBLISHED);
				log.info("Successfully published event ID {} to topic {}", event.getId(), topic);
			} catch (Exception e) {
				event.setStatus(OutboxEvent.OutboxStatus.FAILED);
				log.error("Failed to publish event ID {}", event.getId(), e);
			}
		}
	}
}
