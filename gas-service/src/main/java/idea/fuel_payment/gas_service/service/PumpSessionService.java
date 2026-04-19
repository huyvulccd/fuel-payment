package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.common.SessionCodeGenerator;
import idea.fuel_payment.gas_service.domain.entity.PumpSession;
import idea.fuel_payment.gas_service.domain.enums.SessionStatus;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionRegisterRequest;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionResponse;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionUpdateRequest;
import idea.fuel_payment.gas_service.repository.PumpSessionRepository;
import idea.fuel_payment.gas_service.kafka.dto.SagaStepResponse;
import idea.fuel_payment.gas_service.kafka.dto.StepStatus;
import idea.fuel_payment.gas_service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Service for handling pump session business logic.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PumpSessionService {

    private final PumpSessionRepository pumpSessionRepository;
    private final OutboxService outboxService;

    /**
     * Get pump session information by order code.
     *
     * @param orderCode order code
     * @return pump session information
     * @throws NoSuchElementException if not found
     */
    @Transactional(readOnly = true)
    public PumpSessionResponse getSessionByOrderCode(final String orderCode) {
        PumpSession session = pumpSessionRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new NoSuchElementException(
                        "Pump session not found for order: " + orderCode));
        return mapToResponse(session);
    }

    /**
     * Update pump session status.
     *
     * @param id pump session ID
     * @param request update information
     * @return pump session information after update
     * @throws NoSuchElementException if not found
     */
    @Transactional
    public PumpSessionResponse updateSession(final Long id, final PumpSessionUpdateRequest request) {
        PumpSession session = pumpSessionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Pump session not found: " + id));

        session.setSessionStatus(request.sessionStatus());

        if (request.sessionStatus() == SessionStatus.COMPLETED) {
            session.setCompletedAt(Objects.requireNonNullElseGet(
                    request.completedAt(), LocalDateTime::now));
        }

        PumpSession saved = pumpSessionRepository.save(session);
        log.info("Updated pump session id={} status={}", id, request.sessionStatus());

        return mapToResponse(saved);
    }

    /**
     * Register a new pump session.
     * Automatically generate a unique sessionCode and set status to STARTED.
     *
     * @param request pump session registration information
     * @return registered pump session information
     */
    @Transactional
    public PumpSessionResponse registerSession(final PumpSessionRegisterRequest request) {
        Objects.requireNonNull(request.pumpId(), "pumpId must not be null");
        Objects.requireNonNull(request.orderCode(), "orderCode must not be null");

        String sessionCode = SessionCodeGenerator.generate();
        LocalDateTime now = LocalDateTime.now();

        PumpSession session = PumpSession.builder()
                .sessionCode(sessionCode)
                .pumpId(request.pumpId())
                .orderCode(request.orderCode())
                .licensePlate(request.licensePlate())
                .fuelType(request.fuelType())
                .quantityLiters(request.quantityLiters())
                .unitPrice(request.unitPrice())
                .totalAmount(request.totalAmount())
                .sessionStatus(SessionStatus.STARTED)
                .sagaId(request.sagaId())
                .stepOrder(request.stepOrder())
                .startedAt(now)
                .build();

        PumpSession saved = pumpSessionRepository.save(session);
        log.info("Registered pump session: {} for order: {}", sessionCode, request.orderCode());

        return mapToResponse(saved);
    }

    /**
     * Completes a pump session (called when hardware finishes pumping)
     * and sends a SUCCESS response back to the SAGA orchestrator via Outbox.
     */
    @Transactional
    public PumpSessionResponse completeSessionAndNotifySaga(final Long id, final java.math.BigDecimal quantityLiters) {
        PumpSession session = pumpSessionRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Pump session not found: " + id));

        session.setSessionStatus(SessionStatus.COMPLETED);
        session.setQuantityLiters(quantityLiters);
        session.setTotalAmount(session.getUnitPrice().multiply(quantityLiters));
        session.setCompletedAt(java.time.LocalDateTime.now());

        PumpSession saved = pumpSessionRepository.save(session);
        log.info("Completed pump session: {} for order: {}. Liters: {}, Amount: {}", 
                saved.getSessionCode(), saved.getOrderCode(), quantityLiters, saved.getTotalAmount());

        // Notify SAGA Orchestrator if this session is part of a Saga
        if (saved.getSagaId() != null) {
            SagaStepResponse response = SagaStepResponse.builder()
                    .sagaId(saved.getSagaId())
                    .orderCode(saved.getOrderCode())
                    .stepOrder(saved.getStepOrder())
                    .status(StepStatus.SUCCESS)
                    .payload(java.util.Map.of(
                            "quantityLiters", quantityLiters,
                            "totalAmount", saved.getTotalAmount()
                    ))
                    .timestamp(System.currentTimeMillis())
                    .build();

            outboxService.saveEvent(
                    "SAGA_RESPONSE",
                    saved.getSagaId(),
                    "WAIT_PUMP_COMPLETE_SUCCESS",
                    response
            );
            log.info("Sent SUCCESS response to SAGA for order: {}", saved.getOrderCode());
        }

        return mapToResponse(saved);
    }

    private PumpSessionResponse mapToResponse(final PumpSession session) {
        return new PumpSessionResponse(
                session.getSessionCode(),
                session.getPumpId(),
                session.getOrderCode(),
                session.getLicensePlate(),
                session.getFuelType(),
                session.getQuantityLiters(),
                session.getUnitPrice(),
                session.getTotalAmount(),
                session.getSessionStatus(),
                session.getSagaId(),
                session.getStepOrder(),
                session.getStartedAt(),
                session.getCompletedAt()
        );
    }
}
