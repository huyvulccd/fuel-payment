package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionRegisterRequest;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionResponse;
import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionUpdateRequest;
import idea.fuel_payment.gas_service.service.PumpSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing pump sessions.
 *
 * @author gas-service
 * @version 2026/04/03
 */
@RestController
@RequestMapping("/api/v1/pump-sessions")
@RequiredArgsConstructor
public class PumpSessionController {

    private final PumpSessionService pumpSessionService;

    /**
     * Get pump session information by order code.
     *
     * @param orderCode order code
     * @return pump session information
     */
    @GetMapping("/{orderCode}")
    public ResponseEntity<PumpSessionResponse> getSessionByOrderCode(
            @PathVariable final String orderCode) {
        return ResponseEntity.ok(pumpSessionService.getSessionByOrderCode(orderCode));
    }

    /**
     * Update pump session status.
     *
     * @param id pump session ID
     * @param request update information
     * @return pump session information after update
     */
    @PutMapping("/{id}")
    public ResponseEntity<PumpSessionResponse> updateSession(
            @PathVariable final Long id,
            @Valid @RequestBody final PumpSessionUpdateRequest request) {
        return ResponseEntity.ok(pumpSessionService.updateSession(id, request));
    }

    /**
     * Register a new pump session.
     *
     * @param request registration information
     * @return registered pump session information
     */
    @PostMapping("/register")
    public ResponseEntity<PumpSessionResponse> registerSession(
            @Valid @RequestBody final PumpSessionRegisterRequest request) {
        return ResponseEntity.ok(pumpSessionService.registerSession(request));
    }
}
