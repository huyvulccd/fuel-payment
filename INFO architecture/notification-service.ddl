CREATE DATABASE notification_service_db;
USE notification_service_db;

-- Thông báo
CREATE TABLE notifications (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    notification_code   VARCHAR(40) NOT NULL UNIQUE,
    license_plate       VARCHAR(20) NOT NULL,
    phone               VARCHAR(15) NOT NULL,
    message_type        ENUM('ORDER_CREATED', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'LOW_BALANCE', 'TRANSFER_SUCCESS', 'TRANSFER_FAILED', 'TOPUP_SUCCESS', 'TOPUP_FAILED', 'QUEUE_POSITION') NOT NULL,
    message_content     TEXT NOT NULL,
    channel             ENUM('SMS', 'PUSH', 'EMAIL') NOT NULL,
    send_status         ENUM('PENDING', 'SENT', 'FAILED') DEFAULT 'PENDING',
    sent_at             TIMESTAMP NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_noti_plate ON notifications(license_plate);
CREATE INDEX idx_noti_status ON notifications(send_status);