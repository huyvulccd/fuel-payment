package idea.fuel_payment.payment_service.service;

import idea.fuel_payment.payment_service.domain.common.TransactionCodeGenerator;
import idea.fuel_payment.payment_service.domain.entity.Transaction;
import idea.fuel_payment.payment_service.domain.entity.TransferHistory;
import idea.fuel_payment.payment_service.dto.transfer.TransferRegisterRequest;
import idea.fuel_payment.payment_service.dto.transfer.TransferRegisterResponse;
import idea.fuel_payment.payment_service.repository.TransactionRepository;
import idea.fuel_payment.payment_service.repository.TransferHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Service xử lý nghiệp vụ chuyển tiền.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferHistoryService {

    private final TransactionRepository transactionRepository;
    private final TransferHistoryRepository transferHistoryRepository;

    /**
     * Đăng ký giao dịch chuyển tiền.
     * Tạo Transaction (kind=TRANSFER) và TransferHistory trong cùng một transaction.
     *
     * @param request thông tin chuyển tiền
     * @return kết quả đăng ký chuyển tiền
     * @throws IllegalArgumentException nếu senderId trùng receiverId
     */
    @Transactional
    public TransferRegisterResponse registerTransfer(final TransferRegisterRequest request) {
        Objects.requireNonNull(request.senderId(), "senderId must not be null");
        Objects.requireNonNull(request.receiverId(), "receiverId must not be null");
        Objects.requireNonNull(request.amount(), "amount must not be null");

        if (request.senderId().equals(request.receiverId())) {
            throw new IllegalArgumentException("senderId and receiverId must be different");
        }

        String transactionCode = TransactionCodeGenerator.generate();

        // 1. Tạo Transaction (dùng senderId làm ownerId)
        Transaction transaction = Transaction.builder()
                .ownerId(request.senderId())
                .transactionCode(transactionCode)
                .kind(Transaction.TransactionKind.TRANSFER)
                .status(Transaction.TransactionStatus.PENDING)
                .build();
        transactionRepository.save(transaction);

        // 2. Tạo TransferHistory
        TransferHistory transferHistory = TransferHistory.builder()
                .transactionCode(transactionCode)
                .senderId(request.senderId())
                .receiverId(request.receiverId())
                .amount(request.amount())
                .message(request.message())
                .status(TransferHistory.TransferStatus.PENDING)
                .build();
        transferHistoryRepository.save(transferHistory);

        log.info("Registered transfer transaction: {}", transactionCode);

        return new TransferRegisterResponse(
                transactionCode,
                transferHistory.getSenderId(),
                transferHistory.getReceiverId(),
                transferHistory.getAmount(),
                transferHistory.getMessage(),
                transferHistory.getStatus().name(),
                transferHistory.getSenderBalanceBefore(),
                transferHistory.getSenderBalanceAfter(),
                transferHistory.getReceiverBalanceBefore(),
                transferHistory.getReceiverBalanceAfter()
        );
    }
}
