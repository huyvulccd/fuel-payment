package idea.fuel_payment.orchestrator_service.kafka.producer;

import idea.fuel_payment.orchestrator_service.kafka.dto.SagaCompletedEvent;
import idea.fuel_payment.orchestrator_service.kafka.dto.SagaStepCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class SagaCommandProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topics.saga-completed:fuel.saga.completed}")
	private String sagaCompletedTopic;

	public void sendCommand(String topic, SagaStepCommand command) {
		String key = command.getOrderCode();

		log.info("Sending command: topic={}, key={}, saga={}, " +
						"step={}, getAction={}",
				topic, key, command.getSagaId(),
				command.getStepOrder(), command.getAction());

		CompletableFuture<SendResult<String, Object>> future =
				kafkaTemplate.send(topic, key, command);

		future.whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("Failed to send command: saga={}, " +
								"step={}, error={}",
						command.getSagaId(),
						command.getStepOrder(),
						ex.getMessage());
			} else {
				log.debug("Command sent successfully: " +
								"saga={}, step={}, partition={}, offset={}",
						command.getSagaId(),
						command.getStepOrder(),
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
