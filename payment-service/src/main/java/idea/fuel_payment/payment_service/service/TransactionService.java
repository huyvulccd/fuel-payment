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
 * Service xử lý nghiệp vụ tra cứu giao dịch.
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
     * Lấy danh sách giao dịch theo ownerId có phân trang.
     *
     * @param ownerId ID chủ sở hữu
     * @param limit số bản ghi mỗi trang
     * @param offset vị trí bắt đầu
     * @return danh sách giao dịch phân trang
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
     * Lấy chi tiết giao dịch theo mã giao dịch.
     *
     * @param transactionCode mã giao dịch
     * @return thông tin giao dịch
     * @throws NoSuchElementException nếu không tìm thấy
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
