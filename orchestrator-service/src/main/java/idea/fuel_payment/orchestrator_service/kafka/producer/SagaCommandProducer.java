package idea.fuel_payment.orchestrator_service.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import idea.fuel_payment.orchestrator_service.kafka.dto.SagaStepCommand;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SagaCommandProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topics.saga-completed:fuel.saga.completed}")
	private String sagaCompletedTopic;

	public void sendCommand(String topic, SagaStepCommand command) {
		String key = command.orderCode();

		log.info("Sending command: topic={}, key={}, saga={}, " +
						"step={}, action={}",
				topic, key, command.sagaId(),
				command.stepOrder(), command.action());

		CompletableFuture<SendResult<String, Object>> future =
				kafkaTemplate.send(topic, key, command);

		future.whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("Failed to send command: saga={}, " +
								"step={}, error={}",
						command.sagaId(),
						command.stepOrder(),
						ex.message());
			} else {
				log.debug("Command sent successfully: " +
								"saga={}, step={}, partition={}, offset={}",
						command.sagaId(),
						command.stepOrder(),
						result.getRecordMetadata().partition(),
						result.getRecordMetadata().offset());
			}
		});
	}

	public void sendSagaCompleted(SagaCompletedEvent event) {
		log.info("Sending saga completed: saga={}, order={}, " +
						"status={}",
				event.getSagaId(), event.getOrderCode(),
				event.getFinalStatus());

		kafkaTemplate.send(sagaCompletedTopic,
				event.getOrderCode(), event);
	}
}
