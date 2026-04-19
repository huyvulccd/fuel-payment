package idea.fuel_payment.payment_service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Polling job to automatically relay PENDING outbox events to Kafka.
     * DISABLED by default as per request.
     */
    // @org.springframework.scheduling.annotation.Scheduled(fixedDelay = 5000)
    @Transactional
    public void relayOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxEvent.OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                // Topic format example: {aggregateType}_events
                String topic = event.getAggregateType().toLowerCase() + "_events";
                
                // Publish to Kafka. We use aggregateId as the key for partition affinity.
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());

                // Mark as published
                event.setStatus(OutboxEvent.OutboxStatus.PUBLISHED);
                log.info("Successfully published outbox event ID {} to topic {}", event.getId(), topic);
            } catch (Exception e) {
                event.setStatus(OutboxEvent.OutboxStatus.FAILED);
                log.error("Failed to publish outbox event ID {}", event.getId(), e);
            }
        }
    }
}
