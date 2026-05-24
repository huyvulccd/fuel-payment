package idea.fuel_payment.orchestrator_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idea.fuel_payment.orchestrator_service.engine.SagaEngine;
import idea.fuel_payment.orchestrator_service.kafka.dto.OrderCreatedEvent;
import idea.fuel_payment.orchestrator_service.kafka.dto.SagaStepResponse;
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
public class SagaStepResponseConsumer {

	private final SagaEngine sagaEngine;
	private final ObjectMapper objectMapper;

	@KafkaListener(
			topics = "${kafka.topics.saga-step-response:" +
					"fuel.saga.step.response}",
			groupId = "${spring.kafka.consumer.group-id}",
			containerFactory = "kafkaListenerContainerFactory"
	)
	public void consume(
			@Payload String payload,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.OFFSET) long offset,
			Acknowledgment ack) throws JsonProcessingException {

        SagaStepResponse response =	objectMapper.readValue(payload, SagaStepResponse.class);

		log.info("Received StepResponse: saga={}, step={}, " +
						"status={}, offset={}",
				response.getSagaId(), response.getStepOrder(),
				response.getStatus(), offset);

		try {
			sagaEngine.handleStepResponse(response);
			ack.acknowledge();
		} catch (Exception e) {
			log.error("Failed to process StepResponse: saga={}, " +
							"step={}",
					response.getSagaId(),
					response.getStepOrder(), e);
			throw e;
		}
	}
}