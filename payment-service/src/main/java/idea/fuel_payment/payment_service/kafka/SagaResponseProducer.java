package idea.fuel_payment.payment_service.kafka;

import idea.fuel_payment.payment_service.kafka.dto.SagaStepResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaResponseProducer {

    @Value("${kafka.topics.saga-step-response}")
    String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendResponse(SagaStepResponse response) {
        log.info("Sending SAGA response: sagaId={}, orderCode={}, status={}", 
                response.getSagaId(), response.getOrderCode(), response.getStatus());
        
        kafkaTemplate.send(topic, response.getSagaId(), response)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("SAGA response sent successfully to topic {}", topic);
                    } else {
                        log.error("Failed to send SAGA response", ex);
                    }
                });
    }
}
