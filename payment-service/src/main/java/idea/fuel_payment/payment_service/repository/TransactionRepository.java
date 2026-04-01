package idea.fuel_payment.payment_service.repository;

import idea.fuel_payment.payment_service.domain.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho bảng transactions.
 *
 * @author payment-service
 * @version 2026/04/01
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Tìm danh sách giao dịch theo ownerId có phân trang.
     *
     * @param ownerId ID chủ sở hữu
     * @param pageable thông tin phân trang
     * @return trang kết quả Transaction
     */
    Page<Transaction> findByOwnerId(Long ownerId, Pageable pageable);

    /**
     * Tìm giao dịch theo mã giao dịch.
     *
     * @param transactionCode mã giao dịch
     * @return Optional chứa Transaction nếu tìm thấy
     */
    Optional<Transaction> findByTransactionCode(String transactionCode);
}
