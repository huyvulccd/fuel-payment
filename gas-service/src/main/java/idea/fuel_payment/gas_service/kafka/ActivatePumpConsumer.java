package idea.fuel_payment.gas_service.kafka;

import idea.fuel_payment.gas_service.domain.common.UtilityService;
import idea.fuel_payment.gas_service.dto.fuel_pump.FuelPumpResponse;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionRegisterRequest;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionResponse;
import idea.fuel_payment.gas_service.kafka.dto.SagaStepCommand;
import idea.fuel_payment.gas_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.gas_service.kafka.dto.StepAction;
import idea.fuel_payment.gas_service.kafka.dto.StepStatus;
import idea.fuel_payment.gas_service.service.FuelPumpService;
import idea.fuel_payment.gas_service.service.PumpSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivatePumpConsumer {

	private final SagaResponseProducer responseProducer;

	private final FuelPumpService fuelPumpService;
	private final PumpSessionService pumpSessionService;

	@KafkaListener(
			topics = "${kafka.topics.saga-step-command}",
			groupId = "${spring.kafka.consumer.group-id}"
	)
	public void consume(SagaStepCommand command) {
		if (command.getAction() != StepAction.ACTIVATE_PUMP) {
			return;
		}

		log.info("Received CHECK_BALANCE command for saga: {}, order: {}",
				command.getSagaId(), command.getOrderCode());

		try {
			Map<String, Object> payload = command.getPayload();
			log.debug("Payload in active pump step: {}", payload);

			Long pumpId = Long.valueOf(payload.get("pumpId").toString());
			BigDecimal unitPrice = BigDecimal.valueOf(Long.parseLong(payload.get("unitPrice").toString()));
			String licensePlate = payload.get("licensePlate").toString();
			FuelPumpResponse fuelPumpResponse = fuelPumpService.activatePump(pumpId);

			PumpSessionRegisterRequest pumpSessionRegisterRequest = PumpSessionRegisterRequest
					.builder()
					.pumpId(pumpId)
					.unitPrice(unitPrice)
					.fuelType(fuelPumpResponse.fuelType())
					.licensePlate(licensePlate)
					.orderCode(command.getOrderCode())
					.build();
			PumpSessionResponse pumpSessionResponse = pumpSessionService.registerSession(pumpSessionRegisterRequest);

			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(StepStatus.SUCCESS)
					.payload(UtilityService.toMap(pumpSessionResponse))
					.errorMessage(null)
					.timestamp(System.currentTimeMillis())
					.build();

			responseProducer.sendResponse(response);
		} catch (Exception e) {
			log.error("Error processing check balance step", e);
			SagaStepResponse response = SagaStepResponse.builder()
					.sagaId(command.getSagaId())
					.orderCode(command.getOrderCode())
					.stepOrder(command.getStepOrder())
					.status(StepStatus.FAILED)
					.errorMessage(e.getMessage())
					.timestamp(System.currentTimeMillis())
					.build();
			responseProducer.sendResponse(response);
		}

	}
}
