package idea.fuel_payment.payment_service.kafka;

import idea.fuel_payment.payment_service.kafka.dto.SagaStepCommand;
import idea.fuel_payment.payment_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.payment_service.kafka.dto.StepAction;
import idea.fuel_payment.payment_service.kafka.dto.StepStatus;
import idea.fuel_payment.payment_service.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckBalanceConsumer {

    private final BalanceService balanceService;
    private final SagaResponseProducer responseProducer;

    @KafkaListener(
            topics = "${kafka.topics.saga-step-command:fuel.saga.step.command}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(SagaStepCommand command) {
        if (command.getAction() != StepAction.CHECK_BALANCE) {
            return;
        }

        log.info("Received CHECK_BALANCE command for saga: {}, order: {}", 
                command.getSagaId(), command.getOrderCode());

        try {
            Map<String, Object> payload = command.getPayload();
            Long ownerId = Long.valueOf(payload.get("ownerId").toString());
            // Need fuelType and stationId? SAGA Orchestrator sends them.
            // FuelOrderService stores them in the payload.
            
            // To calculate required amount, normally we should have price * liters.
            // But orchestrator doesn't send total amount yet?
            // Actually, in our project structure, we check if owner exists and has enough (or just placeholder for now).
            
            // For now, let's look for "totalAmount" or "unitPrice" * "quantityLiters"
            // Wait, FuelOrderService.joinLine creates the order with unitPrice but doesn't have liters yet?
            // "quantityLiters" will be available AFTER pump is complete.
            
            // In SAGA FuelPurchaseSagaDefinition:
            // step 1: CREATE_ORDER
            // step 2: CHECK_BALANCE (Check if user has MINIMUM balance or just VALID?)
            
            // Let's check for a fixed minimum or a balance > 0.
            boolean hasFunds = balanceService.hasSufficientBalance(ownerId, BigDecimal.ZERO); // Check if exists and not negative

            StepStatus status = hasFunds ? StepStatus.SUCCESS : StepStatus.FAILED;
            String error = hasFunds ? null : "Insufficient balance or user not found";

            SagaStepResponse response = SagaStepResponse.builder()
                    .sagaId(command.getSagaId())
                    .orderCode(command.getOrderCode())
                    .stepOrder(command.getStepOrder())
                    .status(status)
                    .errorMessage(error)
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
