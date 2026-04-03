CREATE DATABASE orchestrator_service_db;
USE orchestrator_service_db;

-- SAGA instance
CREATE TABLE saga_instances (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    saga_id             VARCHAR(50) NOT NULL UNIQUE,
    saga_type           VARCHAR(50) NOT NULL,
    order_code          VARCHAR(30),
    license_plate       VARCHAR(20),
    payload             JSON NOT NULL,
    saga_status         ENUM('STARTED', 'PROCESSING', 'COMPENSATING', 'COMPLETED', 'FAILED') DEFAULT 'STARTED',
    current_step        INT DEFAULT 0,
    total_steps         INT NOT NULL,
    failure_reason      VARCHAR(500),
    started_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at        TIMESTAMP NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Chi tiết từng bước SAGA
CREATE TABLE saga_step_logs (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    saga_id             VARCHAR(50) NOT NULL,
    step_order          INT NOT NULL,
    step_name           VARCHAR(100) NOT NULL,
    service_name        VARCHAR(50) NOT NULL,
    step_status         ENUM('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'COMPENSATED') DEFAULT 'PENDING',
    request_payload     JSON,
    response_payload    JSON,
    error_message       VARCHAR(500),
    started_at          TIMESTAMP NULL,
    completed_at        TIMESTAMP NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_saga_step (saga_id, step_order)
);

CREATE INDEX idx_saga_status ON saga_instances(saga_status);
CREATE INDEX idx_saga_order ON saga_instances(order_code);
CREATE INDEX idx_step_saga ON saga_step_logs(saga_id);