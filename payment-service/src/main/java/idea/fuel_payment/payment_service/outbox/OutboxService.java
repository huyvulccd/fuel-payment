package idea.fuel_payment.payment_service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * Saves an event into the outbox table.
     * Use MANDATORY propagation to ensure it's part of an existing business transaction.
     */
    @SneakyThrows
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveEvent(String aggregateType, String aggregateId, String eventType, Object payload) {
        String jsonPayload = objectMapper.writeValueAsString(payload);
        
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(jsonPayload)
                .status(OutboxEvent.OutboxStatus.PENDING)
                .build();
        
        outboxEventRepository.save(event);
        log.info("Saved outbox event: {} for aggregate: {}", eventType, aggregateId);
    }
}
