package idea.fuel_payment.payment_service.service;

import idea.fuel_payment.payment_service.domain.common.TransactionCodeGenerator;
import idea.fuel_payment.payment_service.domain.entity.PaymentHistory;
import idea.fuel_payment.payment_service.domain.entity.Transaction;
import idea.fuel_payment.payment_service.dto.payment.PaymentRegisterRequest;
import idea.fuel_payment.payment_service.dto.payment.PaymentRegisterResponse;
import idea.fuel_payment.payment_service.repository.PaymentHistoryRepository;
import idea.fuel_payment.payment_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Service for handling order payment business logic.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentHistoryService {

    private final TransactionRepository transactionRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    /**
     * Register order payment transaction.
     * Create Transaction (kind=PAYMENT) and PaymentHistory in the same transaction.
     *
     * @param request payment information
     * @return payment registration result
     */
    @Transactional
    public PaymentRegisterResponse registerPayment(final PaymentRegisterRequest request) {
        Objects.requireNonNull(request.ownerId(), "ownerId must not be null");
        Objects.requireNonNull(request.orderCode(), "orderCode must not be null");
        Objects.requireNonNull(request.licensePlate(), "licensePlate must not be null");
        Objects.requireNonNull(request.amount(), "amount must not be null");

        String transactionCode = TransactionCodeGenerator.generate();

        // 1. Create Transaction
        Transaction transaction = Transaction.builder()
                .ownerId(request.ownerId())
                .transactionCode(transactionCode)
                .kind(Transaction.TransactionKind.PAYMENT)
                .status(Transaction.TransactionStatus.PENDING)
                .build();
        transactionRepository.save(transaction);

        // 2. Create PaymentHistory
        PaymentHistory paymentHistory = PaymentHistory.builder()
                .transactionCode(transactionCode)
                .ownerId(request.ownerId())
                .orderCode(request.orderCode())
                .licensePlate(request.licensePlate())
                .amount(request.amount())
                .status(PaymentHistory.PaymentStatus.PENDING)
                .build();
        paymentHistoryRepository.save(paymentHistory);

        log.info("Registered payment transaction: {}", transactionCode);

        return new PaymentRegisterResponse(
                transactionCode,
                paymentHistory.getOwnerId(),
                paymentHistory.getOrderCode(),
                paymentHistory.getLicensePlate(),
                paymentHistory.getAmount(),
                paymentHistory.getStatus().name(),
                paymentHistory.getBalanceBefore(),
                paymentHistory.getBalanceAfter()
        );
    }
}
