package idea.fuel_payment.gas_service.controller;

import idea.fuel_payment.gas_service.dto.pump_session.PumpSessionResponse;
import idea.fuel_payment.gas_service.service.PumpSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Mock controller to simulate IoT hardware events (pump completion).
 */
@Slf4j
@RestController
@RequestMapping("/api/mock/pumps")
@RequiredArgsConstructor
public class MockPumpController {

    private final PumpSessionService pumpSessionService;

    /**
     * Simulation of hardware "pump completed" event.
     * In a real system, this might be triggered by a callback from 
     * a hardware controller or an IoT gateway.
     */
    @PostMapping("/sessions/{id}/complete")
    public PumpSessionResponse mockComplete(
            @PathVariable Long id, 
            @RequestParam BigDecimal quantityLiters) {
        
        log.info("MOCK: Hardware signal received: Pump session {} completed with {} liters", id, quantityLiters);
        return pumpSessionService.completeSessionAndNotifySaga(id, quantityLiters);
    }
}
