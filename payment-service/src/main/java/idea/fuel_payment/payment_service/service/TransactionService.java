package idea.fuel_payment.payment_service.service;

import idea.fuel_payment.payment_service.domain.entity.Transaction;
import idea.fuel_payment.payment_service.dto.PageResponse;
import idea.fuel_payment.payment_service.dto.transaction.TransactionResponse;
import idea.fuel_payment.payment_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

/**
 * Service for handling transaction lookup business logic.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Get list of transactions by ownerId with pagination.
     *
     * @param ownerId owner ID
     * @param limit records per page
     * @param offset starting position
     * @return paginated list of transactions
     */
    public PageResponse<TransactionResponse> getTransactionsByOwnerId(
            final Long ownerId, final int limit, final int offset) {
        int page = offset / limit;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> transactionPage = transactionRepository.findByOwnerId(ownerId, pageable);

        return new PageResponse<>(
                transactionPage.getContent().stream()
                        .map(this::mapToResponse)
                        .toList(),
                transactionPage.getTotalElements(),
                page,
                limit
        );
    }

    /**
     * Get transaction details by transaction code.
     *
     * @param transactionCode transaction code
     * @return transaction information
     * @throws NoSuchElementException if not found
     */
    public TransactionResponse getTransactionByCode(final String transactionCode) {
        Transaction transaction = transactionRepository.findByTransactionCode(transactionCode)
                .orElseThrow(() -> new NoSuchElementException(
                        "Transaction not found: " + transactionCode));
        return mapToResponse(transaction);
    }

    private TransactionResponse mapToResponse(final Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionCode(),
                transaction.getOwnerId(),
                transaction.getKind().name(),
                transaction.getStatus().name(),
                transaction.getBalanceBefore(),
                transaction.getBalanceAfter(),
                transaction.getFailureReason(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
