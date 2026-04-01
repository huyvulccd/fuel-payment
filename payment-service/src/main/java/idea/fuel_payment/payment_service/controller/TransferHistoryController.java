package idea.fuel_payment.payment_service.controller;

import idea.fuel_payment.payment_service.dto.transfer.TransferRegisterRequest;
import idea.fuel_payment.payment_service.dto.transfer.TransferRegisterResponse;
import idea.fuel_payment.payment_service.service.TransferHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller đăng ký chuyển tiền.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@RestController
@RequestMapping("/api/v1/transfer-history")
@RequiredArgsConstructor
public class TransferHistoryController {

    private final TransferHistoryService transferHistoryService;

    /**
     * Đăng ký giao dịch chuyển tiền.
     *
     * @param request thông tin chuyển tiền
     * @return kết quả đăng ký
     */
    @PostMapping("/register")
    public TransferRegisterResponse registerTransfer(
            @RequestBody @Valid final TransferRegisterRequest request) {
        return transferHistoryService.registerTransfer(request);
    }
}
