package idea.fuel_payment.orchestrator_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.fuel_payment.orchestrator_service.kafka.dto.SagaStepCommand;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public SagaCommandProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	public void send(String topic, SagaStepCommand command) {
		try {
			String json = objectMapper.writeValueAsString(command);
			kafkaTemplate.send(topic, command.sagaId(), json);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize SagaStepCommand", e);
		}
	}
}
