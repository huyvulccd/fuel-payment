package idea.fuel_payment.payment_service.service;

import idea.fuel_payment.payment_service.domain.common.TransactionCodeGenerator;
import idea.fuel_payment.payment_service.domain.entity.TopupHistory;
import idea.fuel_payment.payment_service.domain.entity.Transaction;
import idea.fuel_payment.payment_service.dto.topup.TopupRegisterRequest;
import idea.fuel_payment.payment_service.dto.topup.TopupRegisterResponse;
import idea.fuel_payment.payment_service.repository.TopupHistoryRepository;
import idea.fuel_payment.payment_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Service for handling top-up business logic.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TopupHistoryService {

    private final TransactionRepository transactionRepository;
    private final TopupHistoryRepository topupHistoryRepository;

    /**
     * Register top-up transaction.
     * Create Transaction (kind=TOPUP) and TopupHistory in the same transaction.
     *
     * @param request top-up information
     * @return top-up registration result
     */
    @Transactional
    public TopupRegisterResponse registerTopup(final TopupRegisterRequest request) {
        Objects.requireNonNull(request.ownerId(), "ownerId must not be null");
        Objects.requireNonNull(request.amount(), "amount must not be null");
        Objects.requireNonNull(request.topupMethod(), "topupMethod must not be null");

        String transactionCode = TransactionCodeGenerator.generate();

        TopupHistory.TopupMethod method = TopupHistory.TopupMethod.valueOf(
                request.topupMethod().toUpperCase());

        // 1. Create Transaction
        Transaction transaction = Transaction.builder()
                .ownerId(request.ownerId())
                .transactionCode(transactionCode)
                .kind(Transaction.TransactionKind.TOPUP)
                .status(Transaction.TransactionStatus.PENDING)
                .build();
        transactionRepository.save(transaction);

        // 2. Create TopupHistory
        TopupHistory topupHistory = TopupHistory.builder()
                .transactionCode(transactionCode)
                .ownerId(request.ownerId())
                .amount(request.amount())
                .topupMethod(method)
                .status(TopupHistory.TopupStatus.PENDING)
                .build();
        topupHistoryRepository.save(topupHistory);

        log.info("Registered topup transaction: {}", transactionCode);

        return new TopupRegisterResponse(
                transactionCode,
                topupHistory.getOwnerId(),
                topupHistory.getAmount(),
                topupHistory.getTopupMethod().name(),
                topupHistory.getStatus().name(),
                topupHistory.getBalanceBefore(),
                topupHistory.getBalanceAfter()
        );
    }
}
