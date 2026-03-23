package idea.fuel_payment.orchestrator_service.domain.entity;

import idea.fuel_payment.orchestrator_service.domain.enums.SagaStatus;
import idea.fuel_payment.orchestrator_service.domain.enums.SagaType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_instances",
		indexes = {
				@Index(name = "idx_saga_status", columnList = "saga_status"),
				@Index(name = "idx_saga_order", columnList = "order_code")
		})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaInstance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "saga_id", nullable = false, unique = true, length = 50)
	private String sagaId;

	@Enumerated(EnumType.STRING)
	@Column(name = "saga_type", nullable = false, length = 50)
	private SagaType sagaType;

	@Column(name = "order_code", length = 30)
	private String orderCode;

	@Column(name = "license_plate", length = 20)
	private String licensePlate;

	@Column(name = "payload", nullable = false, length = 10_000)
	private String payload;

	@Enumerated(EnumType.STRING)
	@Column(name = "saga_status")
	private SagaStatus sagaStatus;

	@Column(name = "current_step")
	private Integer currentStep;

	@Column(name = "total_steps", nullable = false)
	private Integer totalSteps;

	@Column(name = "failure_reason", length = 500)
	private String failureReason;

	@Column(name = "started_at", insertable = false, updatable = false)
	private LocalDateTime startedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", insertable = false, updatable = false)
	private LocalDateTime updatedAt;
}
