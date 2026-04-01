package idea.fuel_payment.payment_service.controller;

import idea.fuel_payment.payment_service.dto.PageResponse;
import idea.fuel_payment.payment_service.dto.transaction.TransactionResponse;
import idea.fuel_payment.payment_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller tra cứu giao dịch.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Lấy danh sách giao dịch theo ownerId có phân trang.
     *
     * @param ownerId ID chủ sở hữu
     * @param limit số bản ghi mỗi trang (mặc định 20)
     * @param offset vị trí bắt đầu (mặc định 0)
     * @return danh sách giao dịch phân trang
     */
    @GetMapping
    public PageResponse<TransactionResponse> getTransactions(
            @RequestParam("owner_id") final Long ownerId,
            @RequestParam(value = "limit", defaultValue = "20") final int limit,
            @RequestParam(value = "offset", defaultValue = "0") final int offset) {
        return transactionService.getTransactionsByOwnerId(ownerId, limit, offset);
    }

    /**
     * Lấy chi tiết giao dịch theo mã giao dịch.
     *
     * @param transactionCode mã giao dịch
     * @return thông tin giao dịch
     */
    @GetMapping("/{transactionCode}")
    public TransactionResponse getTransaction(
            @PathVariable final String transactionCode) {
        return transactionService.getTransactionByCode(transactionCode);
    }
}
