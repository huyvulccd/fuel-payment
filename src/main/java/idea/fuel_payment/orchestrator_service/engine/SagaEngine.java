package idea.fuel_payment.orchestrator_service.engine;

import idea.fuel_payment.orchestrator_service.domain.entity.SagaInstance;
import idea.fuel_payment.orchestrator_service.domain.entity.SagaStepLog;
import idea.fuel_payment.orchestrator_service.domain.enums.SagaStatus;
import idea.fuel_payment.orchestrator_service.domain.enums.SagaType;
import idea.fuel_payment.orchestrator_service.domain.enums.StepAction;
import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import idea.fuel_payment.orchestrator_service.domain.enums.StepStatus;
import idea.fuel_payment.orchestrator_service.domain.repository.SagaInstanceRepository;
import idea.fuel_payment.orchestrator_service.domain.repository.SagaStepLogRepository;
import idea.fuel_payment.orchestrator_service.exception.SagaException;
import idea.fuel_payment.orchestrator_service.kafka.dto.OrderCreatedEvent;
import idea.fuel_payment.orchestrator_service.kafka.dto.PumpCompletedEvent;
import idea.fuel_payment.orchestrator_service.kafka.dto.SagaCompletedEvent;
import idea.fuel_payment.orchestrator_service.kafka.dto.SagaStepCommand;
import idea.fuel_payment.orchestrator_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.orchestrator_service.kafka.producer.SagaCommandProducer;
import idea.fuel_payment.orchestrator_service.lock.SagaLockManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Component
@Slf4j
public class SagaEngine {
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepLogRepository sagaStepLogRepository;
    private final SagaCommandProducer sagaCommandProducer;
    private final SagaLockManager lockManager;
    private final Map<SagaType, SagaDefinition> sagaDefinitions;

    public SagaEngine(
            SagaInstanceRepository sagaInstanceRepository,
            SagaStepLogRepository sagaStepLogRepository,
            SagaCommandProducer sagaCommandProducer,
            SagaLockManager lockManager,
            List<SagaDefinition> definitions) {

        this.sagaInstanceRepository = sagaInstanceRepository;
        this.sagaStepLogRepository = sagaStepLogRepository;
        this.sagaCommandProducer = sagaCommandProducer;
        this.lockManager = lockManager;

        // Register all saga definitions
        this.sagaDefinitions = new HashMap<>();
        definitions.forEach(def ->
                sagaDefinitions.put(def.getType(), def)
        );

        log.info("Registered {} saga definitions: {}",
                sagaDefinitions.size(), sagaDefinitions.keySet());
    }

    // =====================================================
    //  1. START SAGA
    // =====================================================
    @Transactional
    public void startSaga(OrderCreatedEvent event) {
        String orderCode = event.orderCode();

        log.info("Starting SAGA for order: {}", orderCode);
//        MDC.put("orderCode", orderCode);

        // Idempotency check
        if (sagaInstanceRepository.existsByOrderCode(orderCode)) {
            log.warn("SAGA already exists for order: {}", orderCode);
            return;
        }

        // Acquire distributed lock
        String sagaId = generateSagaId();
        boolean locked = lockManager.acquireLock(orderCode);
        if (!locked) {
            log.warn("Cannot acquire lock for order: {}", orderCode);
            return;
        }

        try {
            SagaDefinition definition =
                    sagaDefinitions.get(SagaType.FUEL_PURCHASE);

            // Create Saga Instance
            SagaInstance saga = SagaInstance.builder()
                    .sagaId(sagaId)
                    .sagaType(SagaType.FUEL_PURCHASE)
                    .orderCode(orderCode)
                    .currentStep(1)
                    .totalSteps(definition.getTotalSteps())
                    .sagaStatus(SagaStatus.STARTED)
                    .payload(buildInitialPayload(event))
                    .build();

            sagaInstanceRepository.save(saga);
//            MDC.put("sagaId", sagaId);

            // Step 1 (CREATE_ORDER) đã hoàn thành
            // (Order Service đã tạo order)
            logStepSuccess(saga, 1, StepName.CREATE_ORDER,
                    StepAction.CREATE_ORDER, null);

            log.info("SAGA {} created, Step 1 marked SUCCESS",
                    sagaId);

            // Chuyển sang Step 2
            executeNextStep(saga, 2);

        } catch (Exception e) {
            lockManager.releaseLock(orderCode);
            throw new SagaException(
                    "Failed to start saga for order: " + orderCode, e
            );
        }
    }

    // =====================================================
    //  2. HANDLE STEP RESPONSE (từ các service)
    // =====================================================
    @Transactional
    public void handleStepResponse(SagaStepResponse response) {
        String sagaId = response.sagaId();
        int stepOrder = response.stepOrder();

        log.info("Received step response: saga={}, step={}, status={}",
                sagaId, stepOrder, response.status());
//        MDC.put("sagaId", sagaId);

        SagaInstance saga = sagaInstanceRepository.findBySagaId(sagaId)
                .orElseThrow(() -> new SagaException(
                        "Saga not found: " + sagaId
                ));

        if (saga.isTerminated()) {
            log.warn("Saga {} already terminated, ignoring response",
                    sagaId);
            return;
        }

        // Idempotency: check if step already processed
        Optional<SagaStepLog> existingLog =
                sagaStepLogRepository.findBySagaIdAndStepOrder(
                        sagaId, stepOrder
                );

        if (existingLog.isPresent()
                && existingLog.get().getStepStatus() == StepStatus.SUCCESS) {
            log.warn("Step {} already processed for saga {}",
                    stepOrder, sagaId);
            return;
        }

        if (response.status() == StepStatus.SUCCESS) {
            handleStepSuccess(saga, response);
        } else {
            handleStepFailure(saga, response);
        }
    }

    // =====================================================
    //  3. HANDLE EXTERNAL EVENT
    //     (ví dụ: PumpCompleted)
    // =====================================================
    @Transactional
    public void handlePumpCompleted(PumpCompletedEvent event) {
        String orderCode = event.orderCode();

        log.info("Pump completed for order: {}, quantity: {}, amount: {}",
                orderCode, event.quantityLiters(),
                event.totalAmount());

        SagaInstance saga =
                sagaInstanceRepository.findByOrderCode(orderCode)
                        .orElseThrow(() -> new SagaException(
                                "Saga not found for order: " + orderCode
                        ));

        MDC.put("sagaId", saga.getSagaId());

        // Update payload with pump data
        Map<String, Object> payload = saga.getPayload();
        if (payload == null) payload = new HashMap<>();

        payload.put("quantityLiters",
                event.quantityLiters().toString());
        payload.put("totalAmount",
                event.totalAmount().toString());
        payload.put("sessionCode", event.sessionCode());
        saga.setPayload(payload);

        // Mark Step 4 (WAIT_PUMP_COMPLETE) as SUCCESS
        logStepSuccess(saga, 4, StepName.WAIT_PUMP_COMPLETE,
                StepAction.WAIT_PUMP_COMPLETE,
                Map.of(
                        "quantityLiters",
                        event.quantityLiters().toString(),
                        "totalAmount",
                        event.totalAmount().toString()
                ));

        sagaInstanceRepository.save(saga);

        // Tiến sang Step 5
        executeNextStep(saga, 5);
    }

    // =====================================================
    //  PRIVATE METHODS
    // =====================================================

    private void handleStepSuccess(SagaInstance saga,
                                   SagaStepResponse response) {
        int currentStep = response.stepOrder();

        // Log step success
        updateStepLog(saga.getSagaId(), currentStep,
                StepStatus.SUCCESS, response.payload(),
                null);

        // Update saga
        int nextStep = currentStep + 1;
        saga.advanceToStep(nextStep);

        SagaDefinition definition =
                sagaDefinitions.get(saga.getSagaType());

        if (nextStep > definition.getTotalSteps()) {
            // Tất cả steps hoàn tất
            completeSaga(saga);
        } else {
            sagaInstanceRepository.save(saga);

            SagaStepDefinition nextStepDef =
                    definition.getStep(nextStep).orElseThrow();

            if (nextStepDef.asyncWait()) {
                // Step cần chờ external event
                // (WAIT_PUMP_COMPLETE)
                log.info("Step {} is async wait, waiting for " +
                        "external event...", nextStep);
                logStepInProgress(saga, nextStep,
                        nextStepDef.stepName(),
                        nextStepDef.forwardAction());
            } else {
                executeNextStep(saga, nextStep);
            }
        }
    }

    private void handleStepFailure(SagaInstance saga,
                                   SagaStepResponse response) {
        int failedStep = response.stepOrder();
        String error = response.errorMessage();

        log.error("Step {} failed for saga {}: {}",
                failedStep, saga.getSagaId(), error);

        // Log step failure
        updateStepLog(saga.getSagaId(), failedStep,
                StepStatus.FAILED, null, error);

        // Start compensation
        startCompensation(saga, failedStep, error);
    }

    private void executeNextStep(SagaInstance saga, int stepOrder) {
        SagaDefinition definition =
                sagaDefinitions.get(saga.getSagaType());
        SagaStepDefinition stepDef =
                definition.getStep(stepOrder).orElseThrow(
                        () -> new SagaException(
                                "Step not found: " + stepOrder
                        )
                );

        log.info("Executing step {}: {} for saga {}",
                stepOrder, stepDef.stepName(), saga.getSagaId());

        // Log step in progress
        logStepInProgress(saga, stepOrder,
                stepDef.stepName(),
                stepDef.forwardAction());

        // Build and send command
        SagaStepCommand command = SagaStepCommand.of(
                saga.getSagaId(),
                saga.getOrderCode(),
                stepOrder,
                stepDef.forwardAction(),
                saga.getPayload()
        );

        sagaCommandProducer.sendCommand(
                stepDef.targetTopic(), command
        );

        saga.advanceToStep(stepOrder);
        sagaInstanceRepository.save(saga);
    }

    private void completeSaga(SagaInstance saga) {
        log.info("Completing SAGA: {}", saga.getSagaId());

        saga.complete();
        sagaInstanceRepository.save(saga);

        // Release lock
        lockManager.releaseLock(saga.getOrderCode());

        // Publish completion event
        sagaCommandProducer.sendSagaCompleted(
                SagaCompletedEvent.builder()
                        .sagaId(saga.getSagaId())
                        .orderCode(saga.getOrderCode())
                        .finalStatus(SagaStatus.COMPLETED)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );

        log.info("SAGA {} COMPLETED successfully!", saga.getSagaId());
    }

    // =====================================================
    //  COMPENSATION
    // =====================================================
    private void startCompensation(SagaInstance saga,
                                   int failedStep,
                                   String errorMessage) {
        log.warn("Starting compensation for saga {} from step {}",
                saga.getSagaId(), failedStep);

        saga.startCompensation();
        saga.setErrorMessage(errorMessage);
        sagaInstanceRepository.save(saga);

        SagaDefinition definition =
                sagaDefinitions.get(saga.getSagaType());
        List<SagaStepDefinition> compensationSteps =
                definition.getCompensationSteps(failedStep);

        if (compensationSteps.isEmpty()) {
            log.info("No compensation steps needed for saga {}",
                    saga.getSagaId());
            saga.fail(errorMessage);
            sagaInstanceRepository.save(saga);
            lockManager.releaseLock(saga.getOrderCode());
            return;
        }

        // Execute compensation steps sequentially
        for (SagaStepDefinition compStep : compensationSteps) {
            log.info("Compensating step {}: {}",
                    compStep.stepOrder(),
                    compStep.compensationAction());

            SagaStepCommand command = SagaStepCommand.of(
                    saga.getSagaId(),
                    saga.getOrderCode(),
                    compStep.stepOrder(),
                    compStep.compensationAction(),
                    saga.getPayload()
            );

            sagaCommandProducer.sendCommand(
                    compStep.targetTopic(), command
            );

            // Log compensation step
            SagaStepLog stepLog = sagaStepLogRepository
                    .findBySagaIdAndStepOrder(
                            saga.getSagaId(), compStep.stepOrder()
                    ).orElse(null);

            if (stepLog != null) {
                stepLog.setStepStatus(StepStatus.COMPENSATING);
                sagaStepLogRepository.save(stepLog);
            }
        }
    }

    // =====================================================
    //  LOGGING HELPERS
    // =====================================================
    private void logStepSuccess(SagaInstance saga, int stepOrder,
                                StepName stepName,
                                StepAction action,
                                Map<String, Object> response) {

        SagaStepLog stepLog = sagaStepLogRepository
                .findBySagaIdAndStepOrder(saga.getSagaId(), stepOrder)
                .orElse(SagaStepLog.builder()
                        .sagaId(saga.getSagaId())
                        .stepOrder(stepOrder)
                        .stepName(stepName)
                        .stepAction(action)
                        .build());

        stepLog.markSuccess(response);
        stepLog.setSagaInstance(saga);
        sagaStepLogRepository.save(stepLog);
    }

    private void logStepInProgress(SagaInstance saga, int stepOrder,
                                   StepName stepName,
                                   StepAction action) {

        SagaStepLog stepLog = SagaStepLog.builder()
                .sagaId(saga.getSagaId())
                .stepOrder(stepOrder)
                .stepName(stepName)
                .stepAction(action)
                .build();

        stepLog.markInProgress();
        stepLog.setSagaInstance(saga);
        sagaStepLogRepository.save(stepLog);
    }

    private void updateStepLog(String sagaId, int stepOrder,
                               StepStatus status,
                               Map<String, Object> response,
                               String error) {

        SagaStepLog stepLog = sagaStepLogRepository
                .findBySagaIdAndStepOrder(sagaId, stepOrder)
                .orElseThrow(() -> new SagaException(
                        "Step log not found: " + sagaId + "/" + stepOrder
                ));

        if (status == StepStatus.SUCCESS) {
            stepLog.markSuccess(response);
        } else {
            stepLog.markFailed(error);
        }

        sagaStepLogRepository.save(stepLog);
    }

    private String generateSagaId() {
        return "SAGA" + System.currentTimeMillis()
                + String.format("%04d",
                new Random().nextInt(10000));
    }

    private Map<String, Object> buildInitialPayload(
            OrderCreatedEvent event) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderCode", event.orderCode());
        payload.put("licensePlate", event.licensePlate());
        payload.put("ownerId", event.ownerId());
        payload.put("stationId", event.stationId());
        payload.put("pumpId", event.pumpId());
        payload.put("fuelType", event.fuelType());
        return payload;
    }
}
