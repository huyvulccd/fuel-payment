# ⛽ Fuel Payment System

> A high-performance, microservices-based fuel payment platform designed for scalability, real-time pricing synchronization, and guaranteed transactional integrity — built for the Vietnamese energy sector.

---

## 📦 Microservices Overview

| Service | Responsibility |
|---|---|
| `order-service` | Owner/Vehicle registration, balance management, fuel order lifecycle |
| `gas-service` | Fuel pump control, real-time pricing, inventory tracking |
| `payment-service` | Balance deduction, transaction records, payment history |
| `orchestrator-service` | SAGA coordination across all services |
| `attention-service` | Notifications (SMS, push) to owners |

---

## 🚀 Core Flow — SAGA Orchestration Pattern

The entire fuel purchase lifecycle is managed by a **SAGA Orchestrator** coordinating 8 steps across all services via Kafka. Below are the key phases:

### Phase 1 — Order Creation

```mermaid
sequenceDiagram
    autonumber
    participant Camera as OCR Camera / IoT
    participant Gateway as API Gateway
    participant Order as ORDER SERVICE
    participant Redis as Redis Cache
    participant DB as Database
    participant Kafka as Apache Kafka

    Camera->>Gateway: POST /api/v1/orders {licensePlate, stationId, pumpId, fuelType}
    Gateway->>Order: Forward request

    Order->>Redis: GET vehicle:51F-123.45
    Redis-->>Order: Cache miss

    Order->>DB: SELECT * FROM vehicle WHERE license_plate = '51F-123.45'
    DB-->>Order: Vehicle data (ownerId: 123)

    Order->>Redis: SET vehicle:51F-123.45 TTL 3600s

    Order->>DB: INSERT INTO fuel_order (order_status = 'CREATED')
    DB-->>Order: order_id, order_code

    Order->>Kafka: Produce → fuel.order.created {orderCode, licensePlate, ownerId}
    Order-->>Gateway: 201 Created {orderCode: "ORD20250101000001"}
```

### Phase 2–3 — SAGA Init & Balance Check

```mermaid
sequenceDiagram
    autonumber
    participant Kafka as Apache Kafka
    participant Orchestrator as ORCHESTRATOR
    participant Payment as PAYMENT SERVICE
    participant Redis as Redis Cache
    participant DB as Database

    Kafka-->>Orchestrator: Consume → fuel.order.created
    Orchestrator->>DB: INSERT INTO saga_instances (saga_status='RUNNING', total_steps=8)
    Orchestrator->>Redis: SET saga:lock:ORD... EX 300 NX

    Orchestrator->>Kafka: Produce → fuel.saga.step.command {step:2, action:"CHECK_BALANCE"}

    Kafka-->>Payment: Consume step command
    Payment->>Redis: GET balance:owner:123
    Redis-->>Payment: Cache miss
    Payment->>DB: SELECT balance FROM owners WHERE id=123 → 500,000đ

    Payment->>Payment: Validate 500,000đ ≥ minBalance ✓
    Payment->>Redis: SET balance:owner:123 500000 TTL 300s
    Payment->>Kafka: Produce → fuel.saga.step.response {step:2, status:"SUCCESS"}
```

### Phase 4–5 — Pump Activation & Fueling

```mermaid
sequenceDiagram
    autonumber
    participant Orchestrator as ORCHESTRATOR
    participant Kafka as Apache Kafka
    participant Gas as GAS SERVICE
    participant IoT as IoT Sensor
    participant Redis as Redis Cache
    participant DB as Database

    Orchestrator->>Kafka: Produce → {step:3, action:"ACTIVATE_PUMP"}
    Kafka-->>Gas: Consume step command
    Gas->>Redis: HGET pump:status:PUMP001 → "AVAILABLE"
    Gas->>DB: UPDATE fuel_pumps SET pump_status='IN_USE'
    Gas->>Redis: HSET pump:status:PUMP001 status "IN_USE" current_order "ORD..."
    Gas->>Kafka: Produce → fuel.pump.activated

    Note over IoT: Customer fuels up — 10.5 liters

    IoT->>Gas: POST /api/v1/gas/pumps/1/complete {sessionCode, quantityLiters: 10.5}
    Gas->>Redis: HGET fuel:price:RON95 → 24,500đ/L
    Gas->>Gas: Calculate 10.5 × 24,500 = 257,250đ
    Gas->>DB: UPDATE pump_sessions (quantity=10.5, total_amount=257250, status='COMPLETED')
    Gas->>Kafka: Produce → fuel.pump.completed {sessionCode, orderCode, quantity, amount}
```

### Phase 6–10 — Payment, Inventory, Completion & Notification

```mermaid
sequenceDiagram
    autonumber
    participant Orchestrator as ORCHESTRATOR
    participant Kafka as Apache Kafka
    participant Payment as PAYMENT SERVICE
    participant Gas as GAS SERVICE
    participant Order as ORDER SERVICE
    participant Notif as NOTIFICATION
    participant Redis as Redis Cache
    participant DB as Database

    Orchestrator->>Kafka: Produce → {step:5, action:"PROCESS_PAYMENT", amount:257250}
    Kafka-->>Payment: Consume

    Payment->>Redis: SET payment:lock:owner:123 EX 30 NX
    Payment->>DB: BEGIN TRANSACTION
    Payment->>DB: SELECT balance FROM owners WHERE id=123 FOR UPDATE
    Payment->>Payment: 500,000 - 257,250 = 242,750đ ✓
    Payment->>DB: UPDATE owners SET balance=242750
    Payment->>DB: INSERT INTO payment_history (amount=257250, status='SUCCESS')
    Payment->>DB: COMMIT
    Payment->>Redis: SET balance:owner:123 242750 TTL 300s
    Payment->>Redis: DEL payment:lock:owner:123
    Payment->>Kafka: Produce → fuel.payment.success

    Orchestrator->>Kafka: Produce → {step:6, action:"UPDATE_INVENTORY"}
    Kafka-->>Gas: Consume
    Gas->>DB: UPDATE fuel_inventory SET current_volume = current_volume - 10.5
    Gas->>Redis: HSET inventory:station:1 RON95 49989.5

    Orchestrator->>Kafka: Produce → {step:7, action:"COMPLETE_ORDER"}
    Kafka-->>Order: Consume
    Order->>DB: UPDATE fuel_order SET order_status='COMPLETED', total_amount=257250
    Order->>Redis: HSET order:ORD... status "COMPLETED"

    Orchestrator->>Kafka: Produce → {step:8, action:"SEND_NOTIFICATION"}
    Kafka-->>Notif: Consume
    Notif->>Redis: SADD notification:sent:ORD... "PAYMENT_SUCCESS" → 1 (not duplicate)
    Notif->>Notif: "Thanh toán thành công 257,250đ — 10.5L RON95. Số dư: 242,750đ"
    Notif->>DB: INSERT INTO notifications (send_status='SENT')

    Orchestrator->>DB: UPDATE saga_instances SET saga_status='COMPLETED'
    Orchestrator->>Redis: DEL saga:lock:ORD...
    Orchestrator->>Kafka: Produce → fuel.saga.completed
    Note over Orchestrator: ✅ Total time: ~5 seconds
```

---

## ⚡ Redis — Full Capability Map

Redis is used across **5 distinct patterns** in this system:

### 1. Application Cache (L1 Distributed Cache)

| Key Pattern | Value | TTL | Purpose |
|---|---|---|---|
| `vehicle:{licensePlate}` | Vehicle JSON | 3600s | Avoid DB lookup on every order |
| `balance:owner:{id}` | Long (VNĐ) | 300s | O(1) balance reads in payment checks |
| `order:{orderCode}` | Order status | 1800s | Fast status lookups |

### 2. JPA Level 2 (L2) Cache — Master Data

Redis acts as the **shared Hibernate L2 cache** for rarely-changing master data:

- **`VehicleModel`** — all running instances share a single cached copy
- Eliminates N+1 query problems for vehicle-model joins
- Automatic invalidation when admin updates model data
- Configured via `spring-boot-starter-data-redis` + Hibernate RegionFactory

### 3. Distributed Locking (Redlock)

| Lock Key | Held By | TTL | Purpose |
|---|---|---|---|
| `saga:lock:{orderCode}` | Orchestrator | 300s | Prevent duplicate SAGA execution |
| `payment:lock:owner:{id}` | Payment Service | 30s | Prevent race condition on balance deduction |
| `pump:lock:{pumpId}` | Gas Service | 60s | Prevent double-activation of same pump |

> **Pattern:** `SET key value EX {ttl} NX` — atomic acquire. Released with `DEL` after work completes.

### 4. Real-Time State (Hash Maps)

```
HSET pump:status:PUMP001
    status       "IN_USE"
    current_order "ORD20250101000001"
    activated_at  "2025-01-01T10:00:00Z"

HGET fuel:price:RON95 → 24500
HGET inventory:station:1 RON95 → 49989.5
```

### 5. Idempotency Guard & Rate Limiting

- `SADD notification:sent:{orderCode} "PAYMENT_SUCCESS"` — prevents duplicate SMS/push
- Sliding window rate limiter: `ZADD + ZCOUNT` on `rate_limit:ip:{ip}` to throttle API abuse
- Future: `INCR + EXPIRE` counter for request-per-second limits per endpoint

---

## 📡 Kafka — Full Capability Map

### Topic Registry

| Topic | Producer | Consumer | Payload |
|---|---|---|---|
| `fuel.order.created` | Order Service | Orchestrator | `{orderCode, licensePlate, ownerId, fuelType}` |
| `fuel.saga.step.command` | Orchestrator | Payment / Gas / Order / Notif | `{step, action, sagaId, payload}` |
| `fuel.saga.step.response` | All services | Orchestrator | `{step, sagaId, status, data}` |
| `fuel.pump.activated` | Gas Service | Notification | `{pumpId, sessionCode}` |
| `fuel.pump.completed` | Gas Service | Orchestrator | `{sessionCode, quantity, amount}` |
| `fuel.payment.success` | Payment Service | Notification / Analytics | `{transactionCode, amount, newBalance}` |
| `fuel.inventory.updated` | Gas Service | Analytics / Dashboard | `{stationId, fuelType, newVolume}` |
| `fuel.saga.completed` | Orchestrator | Audit / Analytics | `{sagaId, orderCode, durationMs}` |
| `fuel-price-topic` | Gas Service | Order Service | `PriceChangedEvent {fuelType, newPrice}` |

### Reliability — Outbox Pattern

```mermaid
flowchart LR
    A[Business Logic] -->|Same Transaction| B[(outbox_events table)]
    B --> C[Background Scheduler\npolls every 1s]
    C -->|Publish| D[Apache Kafka]
    D --> E[Consumer Services]
    C -->|On fail| F[Retry with\nExponential Backoff]
```

> **Guarantee:** Events are never lost even if the service crashes between DB write and Kafka publish. Achieves **At-Least-Once Delivery**.

### SAGA Error Compensation

```mermaid
flowchart TD
    S1[Step 1: CREATE_ORDER ✓] --> S2[Step 2: CHECK_BALANCE ✓]
    S2 --> S3[Step 3: ACTIVATE_PUMP ✓]
    S3 --> S4[Step 4: WAIT_PUMP_COMPLETE ✓]
    S4 --> S5[Step 5: PROCESS_PAYMENT ✗ FAILED]
    S5 -->|Compensation| C1[Deactivate Pump]
    C1 --> C2[Cancel Order → status=FAILED]
    C2 --> C3[Notify Owner — Payment Failed]
```

---

## 🛠️ Performance & Concurrency

### Locking Strategy

| Resource | Lock Type | Reason |
|---|---|---|
| `Owner.balance` | **Pessimistic** (`SELECT ... FOR UPDATE`) | Prevents over-spending in concurrent pumping sessions |
| `VehicleModel` | **Optimistic** (`@Version`) | Low-conflict master data; avoids lock overhead |
| `pump activation` | **Redis Distributed Lock** | Cross-service coordination, no shared DB |

### Async Processing

- **`CompletableFuture` + `TaskExecutor`** — Notification sending (SMS, push) runs in parallel without blocking the payment response.
- **Scheduled polling** — Outbox worker uses `@Scheduled(fixedDelay = 1000)` to flush pending events.
- **HikariCP tuning** — Connection pool configured for sustained high concurrency during peak hours.

---

## 📈 Extension Technologies

### 1. ELK Stack — Real-Time Analytics

```mermaid
flowchart LR
    Kafka[Apache Kafka\nfuel.payment.success\nfuel.inventory.updated] -->|Kafka Connect| ES[(Elasticsearch)]
    ES --> Kibana[Kibana Dashboard]
    Kibana --> D1[Fuel consumption\nby region/station]
    Kibana --> D2[Revenue trends\nper hour/day]
    Kibana --> D3[Inventory alerts\nlow stock warning]
```

**Use Case:** Operations team monitors nationwide fuel consumption trends in real time without impacting the transactional database.

### 2. BFF + Redis Session — Multi-Platform UX

```mermaid
flowchart LR
    Mobile[Mobile App] --> BFF[BFF Layer\nSpring Cloud Gateway]
    Web[Web App] --> BFF
    BFF --> Redis[(Redis\nSession Store)]
    BFF --> Services[Microservices]
```

- Redis stores user session tokens — all BFF instances share the same session state.
- BFF aggregates multiple service calls into a single optimized API response per client type.

### 3. IoT Integration — Pump Session Management

| Redis Feature | Use Case |
|---|---|
| `HSET pump:status:{id}` | Real-time pump state (AVAILABLE / IN_USE / ERROR) |
| `BITSET pumping:{date}:{hour}` | HyperLogLog count of unique active pumps per hour |
| `TTL` on session locks | Auto-release stuck sessions after timeout |

**Scale target:** Support millions of concurrent `pumping_session` events across thousands of stations.

### 4. Redis Redlock — Zero-Downtime Price Updates

When gas prices change nationally, `minBalance` must be recalculated for all registered vehicles:

```mermaid
sequenceDiagram
    participant Gas as GAS SERVICE
    participant Kafka as Kafka
    participant Order as ORDER SERVICE (N instances)
    participant Redis as Redis Redlock

    Gas->>Kafka: Produce → fuel-price-topic {RON95: 25000}
    Kafka-->>Order: All instances consume
    Order->>Redis: Acquire Redlock (price-update:RON95)
    Note over Redis: Only 1 instance wins the lock
    Order->>Order: Recalculate minBalance for all vehicles
    Order->>Redis: UPDATE current_fuel_price
    Order->>Redis: Release lock
```

### 5. Testcontainers — Integration Testing

```java
@Testcontainers
class FuelOrderIntegrationTest {
    @Container static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8");
    @Container static GenericContainer<?> redis = new GenericContainer<>("redis:7");
    @Container static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7"));
}
```

Full integration tests run against real containers — no mocking of infrastructure.

### 6. Observability Stack

| Tool | Role |
|---|---|
| **Prometheus + Grafana** | JVM metrics, Kafka consumer lag, Redis hit rate |
| **Spring Actuator** | Health check endpoints for orchestrator SAGA status |
| **Distributed Tracing (Zipkin/Jaeger)** | End-to-end trace per `orderCode` across all 5 services |

---

## 🏗️ Infrastructure

```yaml
# docker-compose services
services:
  mysql:        image: mysql:8
  redis:        image: redis:7-alpine  (with AOF persistence)
  kafka:        image: confluentinc/cp-kafka:7
  zookeeper:    image: confluentinc/cp-zookeeper:7
```

---

## 🗺️ Development Roadmap

| Phase | Weeks | Focus |
|---|---|---|
| **1 — Core Records** | 1–3 | Owner, Vehicle, VehicleModel entities & validation |
| **2 — Real-Time State** | 4–6 | Redis caching, price sync, `minBalance` calculation |
| **3 — Event-Driven** | 7–9 | Kafka producers/consumers, Outbox pattern |
| **4 — High-Concurrency** | 10–12 | Locking, pump sessions, async notifications |
| **5 — Production** | 13–15 | Integration tests, rate limiting, deployment |

---

> [!NOTE]
> This project is a demonstration of backend engineering excellence — combining **SAGA orchestration**, **distributed caching**, **event-driven architecture**, and **concurrency control** in the Vietnamese energy sector.
