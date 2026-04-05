package idea.fuel_payment.payment_service.service;

import idea.fuel_payment.payment_service.domain.entity.Balance;
import idea.fuel_payment.payment_service.domain.entity.TransactionType;
import idea.fuel_payment.payment_service.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public BigDecimal getBalance(Long ownerId) {
        return balanceRepository.findByOwnerId(ownerId)
                .map(Balance::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void updateBalance(Long ownerId, BigDecimal amount, TransactionType type) {
        log.info("Updating balance for owner {}: {} ({})", ownerId, amount, type);
        
        Balance balance = balanceRepository.findByOwnerId(ownerId)
                .orElseGet(() -> Balance.builder()
                        .ownerId(ownerId)
                        .balance(BigDecimal.ZERO)
                        .build());

        BigDecimal currentBalance = balance.getBalance();
        BigDecimal newBalance;

        switch (type) {
            case TOPUP -> newBalance = currentBalance.add(amount);
            case PAYMENT, TRANSFER -> {
                if (currentBalance.compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient balance for owner " + ownerId);
                }
                newBalance = currentBalance.subtract(amount);
            }
            default -> throw new IllegalArgumentException("Unknown transaction type: " + type);
        }

        balance.setBalance(newBalance);
        balanceRepository.save(balance);
        log.info("Balance updated for owner {}. New balance: {}", ownerId, newBalance);
    }

    public boolean hasSufficientBalance(Long ownerId, BigDecimal amount) {
        BigDecimal currentBalance = getBalance(ownerId);
        boolean sufficient = currentBalance.compareTo(amount) >= 0;
        log.info("Checking balance for owner {}: required={}, current={}, sufficient={}", 
                ownerId, amount, currentBalance, sufficient);
        return sufficient;
    }
}
