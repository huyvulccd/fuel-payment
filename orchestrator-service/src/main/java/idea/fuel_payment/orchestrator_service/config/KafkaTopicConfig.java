package idea.fuel_payment.orchestrator_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
	String STEP_COMMAND = "fuel.saga.step.command";
	String COMPLETED = "fuel.saga.completed";

	@Bean
	public NewTopic sagaStepCommandTopic() {
		return TopicBuilder.name(STEP_COMMAND)
				.partitions(6)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic sagaCompletedTopic() {
		return TopicBuilder.name(COMPLETED)
				.partitions(3)
				.replicas(1)
				.build();
	}
}
