# 15-Week Project Completion Checklist

This checklist focuses on **completing** the **Fuel Payment System** features while integrating **JPA, Redis, Kafka, and Concurrency** as required.

## Phase 1: Core Records & Persistence (Weeks 1-3)
*Goal: Complete the foundation for Owners, Vehicles, and Models.*
- [ ] **Week 1: Entity & Database Foundation**
    - Complete mapping for `Owner`, `Vehicle`, `VehicleModel`.
    - Implnemet `UtilityService` and base validation logic.
- [ ] **Week 2: Lifecycle Management**
    - Complete `registerOwner`, `updateOwner`, and search by `idPersonal`.
    - Implement the `TotalEnergy` table to track cumulative capacities.
- [ ] **Week 3: Master Data & Constraints**
    - Complete SQL data initialization (`order-service-data.init`).
    - Implement uniqueness constraints (`@ValidatorUnique`) for all core records.

## Phase 2: Real-Time State & Performance (Weeks 4-6)
*Goal: Complete the fast-access caching layer.*
- [ ] **Week 4: Redis Infrastructure**
    - Complete `docker-compose` setup with Redis persistence.
    - Implement `RedisTool` for standardized access across the service.
- [ ] **Week 5: Price Synchronization Layer**
    - Complete `registerVehicle` with automated `minBalance` calculation.
    - Implement logic to fetch real-time gas prices from Redis.
- [ ] **Week 6: Caching & Optimization**
    - Implement caching for `VehicleModel` to reduce DB load.
    - Resolve N+1 query problems in vehicle/owner relationship fetches.

## Phase 3: Event-Driven Interoperability (Weeks 7-9)
*Goal: Complete cross-service communication via Kafka.*
- [ ] **Week 7: Kafka Messaging Framework**
    - Complete Kafka producer/consumer configuration in Spring Boot.
    - Define cross-service events (e.g., `PriceChangedEvent`).
- [ ] **Week 8: Gas-Order Integration**
    - Complete Kafka consumer for `fuel-price-topic`.
    - Enable background updates to Redis whenever `gas-service` pushes price changes.
- [ ] **Week 9: Transactional Reliability**
    - Complete implementation of the `outbox_events` logging.
    - Implement a background scheduler to resend failed events.

## Phase 4: High-Concurrency & Accuracy (Weeks 10-12)
*Goal: Complete thread-safe billing and pumping sessions.*
- [ ] **Week 10: Billing & Balance Protection**
    - Implement locking mechanisms (Optimistic/Pessimistic) on `Owner.balance`.
    - Prevent double-billing scenarios during concurrent pumping.
- [ ] **Week 11: Pumping Session Logic**
    - Complete processing of IoT sensor data (pump sessions).
    - Handle session timeouts and recovery using Redis TTL.
- [ ] **Week 12: Scaling Background Processes**
    - Complete `CompletableFuture` implementation for parallel notification sending.
    - Implement connection pool (HikariCP) tuning for high load.

## Phase 5: Final Polish & Production Readiness (Weeks 13-15)
*Goal: Complete testing, security, and final deliverables.*
- [ ] **Week 13: Integrated Stress Testing**
    - Complete integration tests using `Testcontainers` (MySQL + Redis + Kafka).
    - Fix all edge cases uncovered by stress testing pumping sessions.
- [ ] **Week 14: API Protection & Security**
    - Complete distributed rate limiting using Redis (protect against bots).
    - Secure sensitive owner data endpoints.
- [ ] **Week 15: Final Deployment & Audit**
    - Complete project documentation and API reference.
    - Final verification of the `total_energy` audit trail.
    - Deploy via production-ready Docker Compose.

---
> [!IMPORTANT]
> This roadmap focuses on **functional deliverables**. Technical learning should happen "on-the-job" as you complete each task.
