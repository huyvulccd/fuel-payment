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
 * Controller for transaction lookups.
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
     * Get list of transactions by ownerId with pagination.
     *
     * @param ownerId owner ID
     * @param limit records per page (default 20)
     * @param offset starting position (default 0)
     * @return paginated list of transactions
     */
    @GetMapping
    public PageResponse<TransactionResponse> getTransactions(
            @RequestParam("owner_id") final Long ownerId,
            @RequestParam(value = "limit", defaultValue = "20") final int limit,
            @RequestParam(value = "offset", defaultValue = "0") final int offset) {
        return transactionService.getTransactionsByOwnerId(ownerId, limit, offset);
    }

    /**
     * Get transaction details by transaction code.
     *
     * @param transactionCode transaction code
     * @return transaction information
     */
    @GetMapping("/{transactionCode}")
    public TransactionResponse getTransaction(
            @PathVariable final String transactionCode) {
        return transactionService.getTransactionByCode(transactionCode);
    }
}
