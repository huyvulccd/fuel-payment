package idea.fuel_payment.payment_service.controller;

import idea.fuel_payment.payment_service.dto.payment.PaymentRegisterRequest;
import idea.fuel_payment.payment_service.dto.payment.PaymentRegisterResponse;
import idea.fuel_payment.payment_service.service.PaymentHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller đăng ký thanh toán đơn hàng.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@RestController
@RequestMapping("/api/v1/payment-history")
@RequiredArgsConstructor
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    /**
     * Đăng ký giao dịch thanh toán đơn hàng.
     *
     * @param request thông tin thanh toán
     * @return kết quả đăng ký
     */
    @PostMapping("/register")
    public PaymentRegisterResponse registerPayment(
            @RequestBody @Valid final PaymentRegisterRequest request) {
        return paymentHistoryService.registerPayment(request);
    }
}
