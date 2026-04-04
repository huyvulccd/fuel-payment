package idea.fuel_payment.payment_service.controller;

import idea.fuel_payment.payment_service.dto.topup.TopupRegisterRequest;
import idea.fuel_payment.payment_service.dto.topup.TopupRegisterResponse;
import idea.fuel_payment.payment_service.service.TopupHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for top-up registration.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@RestController
@RequestMapping("/api/v1/topup-history")
@RequiredArgsConstructor
public class TopupHistoryController {

    private final TopupHistoryService topupHistoryService;

    /**
     * Register top-up transaction.
     *
     * @param request top-up information
     * @return registration result
     */
    @PostMapping("/register")
    public TopupRegisterResponse registerTopup(
            @RequestBody @Valid final TopupRegisterRequest request) {
        return topupHistoryService.registerTopup(request);
    }
}
