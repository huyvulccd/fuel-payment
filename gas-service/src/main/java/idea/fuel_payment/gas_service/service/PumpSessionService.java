package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.common.SessionCodeGenerator;
import idea.fuel_payment.gas_service.domain.entity.PumpSession;
import idea.fuel_payment.gas_service.domain.enums.SessionStatus;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionRegisterRequest;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionResponse;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionUpdateRequest;
import idea.fuel_payment.gas_service.repository.PumpSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .startedAt(now)
                .build();

        PumpSession saved = pumpSessionRepository.save(session);
        log.info("Registered pump session: {} for order: {}", sessionCode, request.orderCode());

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
                session.getStartedAt(),
                session.getCompletedAt()
        );
    }
}
