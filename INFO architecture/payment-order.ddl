CREATE TABLE transactions (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id            BIGINT NOT NULL,
    transaction_code    VARCHAR(40) NOT NULL UNIQUE,
    kind                VARCHAR(30) NOT NULL,  -- topup, payment, transfer
    status              ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    balance_before      DECIMAL(15, 2),
    balance_after       DECIMAL(15, 2),
    failure_reason      VARCHAR(255),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_txn_kind (transaction_code, kind)
);

-- Lịch sử nạp tiền
CREATE TABLE topup_history (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_code    VARCHAR(40) NOT NULL UNIQUE,
    owner_id            BIGINT NOT NULL,
    amount              DECIMAL(15, 2) NOT NULL,
    topup_method        ENUM('BANK_TRANSFER', 'MOMO', 'VNPAY') NOT NULL,
    status              ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    balance_before      DECIMAL(15, 2),
    balance_after       DECIMAL(15, 2),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_topup_txn FOREIGN KEY (transaction_code) REFERENCES transactions(transaction_code)
);

-- Lịch sử thanh toán (Thanh toán đơn hàng)
CREATE TABLE payment_history (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_code    VARCHAR(40) NOT NULL UNIQUE,
    owner_id            BIGINT NOT NULL,
    order_code          VARCHAR(30) NOT NULL,
    license_plate       VARCHAR(20) NOT NULL,
    amount              DECIMAL(15, 2) NOT NULL,
    status              ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    balance_before      DECIMAL(15, 2),
    balance_after       DECIMAL(15, 2),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_txn FOREIGN KEY (transaction_code) REFERENCES transactions(transaction_code)
);

-- Lịch sử chuyển tiền
CREATE TABLE transfer_history (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_code    VARCHAR(40) NOT NULL UNIQUE,
    sender_id           BIGINT NOT NULL,
    receiver_id         BIGINT NOT NULL,
    amount              DECIMAL(15, 2) NOT NULL,
    message             VARCHAR(255),
    status              ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    sender_balance_before   DECIMAL(15, 2),
    sender_balance_after    DECIMAL(15, 2),
    receiver_balance_before DECIMAL(15, 2),
    receiver_balance_after  DECIMAL(15, 2),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transfer_txn FOREIGN KEY (transaction_code) REFERENCES transactions(transaction_code)
);

-- Indexes 
CREATE INDEX idx_transactions_owner ON transactions(owner_id);

CREATE INDEX idx_payment_history_order ON payment_history(order_code);
CREATE INDEX idx_payment_history_plate ON payment_history(license_plate);
CREATE INDEX idx_payment_history_owner ON payment_history(owner_id);

CREATE INDEX idx_topup_owner ON topup_history(owner_id);

CREATE INDEX idx_transfer_sender ON transfer_history(sender_id);
CREATE INDEX idx_transfer_receiver ON transfer_history(receiver_id);