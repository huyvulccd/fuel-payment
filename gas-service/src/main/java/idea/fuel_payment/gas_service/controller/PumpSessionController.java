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
 * Controller quản lý phiên bơm xăng.
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
     * Lấy thông tin phiên bơm theo mã đơn hàng.
     *
     * @param orderCode mã đơn hàng
     * @return thông tin phiên bơm
     */
    @GetMapping("/{orderCode}")
    public ResponseEntity<PumpSessionResponse> getSessionByOrderCode(
            @PathVariable final String orderCode) {
        return ResponseEntity.ok(pumpSessionService.getSessionByOrderCode(orderCode));
    }

    /**
     * Cập nhật trạng thái phiên bơm.
     *
     * @param id ID phiên bơm
     * @param request thông tin cập nhật
     * @return thông tin phiên bơm sau cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<PumpSessionResponse> updateSession(
            @PathVariable final Long id,
            @Valid @RequestBody final PumpSessionUpdateRequest request) {
        return ResponseEntity.ok(pumpSessionService.updateSession(id, request));
    }

    /**
     * Đăng ký phiên bơm mới.
     *
     * @param request thông tin đăng ký
     * @return thông tin phiên bơm đã đăng ký
     */
    @PostMapping("/register")
    public ResponseEntity<PumpSessionResponse> registerSession(
            @Valid @RequestBody final PumpSessionRegisterRequest request) {
        return ResponseEntity.ok(pumpSessionService.registerSession(request));
    }
}
