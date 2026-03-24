package idea.fuel_payment.orchestrator_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "idea.fuel_payment.orchestrator_service.domain.repository")
public class JpaConfig {
}
