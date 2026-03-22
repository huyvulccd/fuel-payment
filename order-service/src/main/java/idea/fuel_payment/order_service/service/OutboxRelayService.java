package idea.fuel_payment.order_service.service;

import idea.fuel_payment.order_service.domain.enity.OutboxEvent;
import idea.fuel_payment.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

	private final OutboxEventRepository outboxEventRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;

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
				// Publish to Kafka.
				// Topic format example: fuelorder_events
				String topic = event.getAggregateType().toLowerCase() + "_events";
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
