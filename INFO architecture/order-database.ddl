CREATE TABLE owners (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_personal         VARCHAR(20) NOT NULL UNIQUE COMMENT 'CCCD/CMND',
    name                VARCHAR(100) NOT NULL,
    phone               VARCHAR(15) NOT NULL,
    email               VARCHAR(100),
    address             VARCHAR(255),
    balance             DECIMAL(15, 2) DEFAULT 0.00,
    min_balance         DECIMAL(15, 2) DEFAULT 50000.00,
    status              ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_owners_phone ON owners(phone);
CREATE INDEX idx_owners_status ON owners(status);

CREATE TABLE vehicle_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type_vehicle VARCHAR(50) NOT NULL,
    fuel_type VARCHAR(20),
    capacity_fuel DECIMAL(10, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE total_energy (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL UNIQUE,
    ron95 DECIMAL(15, 3) DEFAULT 0.000,
    e5 DECIMAL(15, 3) DEFAULT 0.000,
    diesel DECIMAL(15, 3) DEFAULT 0.000,
    electronic DECIMAL(15, 3) DEFAULT 0.000,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_energy_owner FOREIGN KEY (owner_id) REFERENCES owners(id)
);

CREATE TABLE vehicle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL,
    id_vehicle_model BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_owner FOREIGN KEY (owner_id) REFERENCES owners(id),
    CONSTRAINT fk_vehicle_model FOREIGN KEY (id_vehicle_model) REFERENCES vehicle_model(id)
);

CREATE TABLE fuel_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(30) NOT NULL UNIQUE,
    vehicle_id BIGINT NOT NULL,
    license_plate VARCHAR(20) NOT NULL,
    station_id BIGINT NOT NULL,
    pump_id BIGINT NOT NULL,
    fuel_type VARCHAR(20) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity_liters DECIMAL(10, 3) DEFAULT 0.000,
    total_amount DECIMAL(15, 2) DEFAULT 0.00,
    order_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);

CREATE TABLE outbox_events (
    id VARCHAR(36) PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);