package idea.fuel_payment.gas_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GasServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GasServiceApplication.class, args);
	}

}
