CREATE DATABASE gas_service_db;
USE gas_service_db;

-- Trạm xăng
CREATE TABLE gas_stations (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_code        VARCHAR(20) NOT NULL UNIQUE,
    station_name        VARCHAR(100) NOT NULL,
    status              ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE') DEFAULT 'ACTIVE',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trụ bơm
CREATE TABLE fuel_pumps (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id          BIGINT NOT NULL,
    pump_number         INT NOT NULL,
    fuel_type           ENUM('RON95', 'E5', 'DIESEL') NOT NULL,
    pump_status         ENUM('AVAILABLE', 'IN_USE', 'OUT_OF_FUEL', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (station_id) REFERENCES gas_stations(id)
);

-- Giá xăng
CREATE TABLE fuel_prices (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    fuel_type           ENUM('RON95', 'E5', 'DIESEL') NOT NULL,
    price               DECIMAL(10, 2) NOT NULL,
    effective_from      TIMESTAMP NOT NULL,
    is_current          BOOLEAN DEFAULT TRUE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Lịch sử bơm xăng (từ IoT sensor)
CREATE TABLE pump_sessions (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_code        VARCHAR(40) NOT NULL UNIQUE,
    pump_id             BIGINT NOT NULL,
    order_code          VARCHAR(30),
    license_plate       VARCHAR(20),
    fuel_type           ENUM('RON95', 'E5', 'DIESEL') NOT NULL,
    quantity_liters     DECIMAL(10, 3) NOT NULL,
    unit_price          DECIMAL(10, 2) NOT NULL,
    total_amount        DECIMAL(15, 2) NOT NULL,
    session_status      ENUM('STARTED', 'PUMPING', 'COMPLETED', 'ERROR') DEFAULT 'STARTED',
    started_at          TIMESTAMP NOT NULL,
    completed_at        TIMESTAMP NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pump_id) REFERENCES fuel_pumps(id)
);

-- Tồn kho xăng
CREATE TABLE fuel_inventory (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    station_id          BIGINT NOT NULL,
    fuel_type           ENUM('RON95', 'E5', 'DIESEL') NOT NULL,
    current_volume      DECIMAL(12, 3) NOT NULL,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (station_id) REFERENCES gas_stations(id),
    UNIQUE KEY uk_station_fuel (station_id, fuel_type)
);

CREATE INDEX idx_pumps_station ON fuel_pumps(station_id);
CREATE INDEX idx_sessions_order ON pump_sessions(order_code);
CREATE INDEX idx_prices_current ON fuel_prices(fuel_type, is_current);