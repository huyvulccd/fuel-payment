package idea.fuel_payment.orchestrator_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

	@Bean
	public NewTopic orderCreatedTopic(@Value("${app.kafka.topics.order-created}") String name) {
		return TopicBuilder.name(name).build();
	}

	@Bean
	public NewTopic sagaStepResponseTopic(@Value("${app.kafka.topics.saga-step-response}") String name) {
		return TopicBuilder.name(name).build();
	}

	@Bean
	public NewTopic pumpCompletedTopic(@Value("${app.kafka.topics.pump-completed}") String name) {
		return TopicBuilder.name(name).build();
	}

	@Bean
	public NewTopic sagaCommandTopic(@Value("${app.kafka.topics.saga-command}") String name) {
		return TopicBuilder.name(name).build();
	}
}
