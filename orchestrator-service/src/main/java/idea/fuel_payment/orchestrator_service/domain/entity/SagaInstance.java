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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "saga_instances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaInstance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "saga_id", nullable = false, unique = true,
			length = 50)
	private String sagaId;

	@Enumerated(EnumType.STRING)
	@Column(name = "saga_type", nullable = false, length = 50)
	private SagaType sagaType;

	@Column(name = "order_code", nullable = false, length = 50)
	private String orderCode;

	@Column(name = "current_step", nullable = false)
	private Integer currentStep;

	@Column(name = "total_steps", nullable = false)
	private Integer totalSteps;

	@Enumerated(EnumType.STRING)
	@Column(name = "saga_status", nullable = false, length = 20)
	private SagaStatus sagaStatus = SagaStatus.STARTED;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "payload")
	private Map<String, Object> payload;

	@Column(name = "error_message", columnDefinition = "TEXT")
	private String errorMessage;

	@Column(name = "started_at", nullable = false)
	private LocalDateTime startedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// ==========================================
	// Domain Methods
	// ==========================================
	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		if (createdAt == null) createdAt = now;
		if (startedAt == null) startedAt = now;
		if (updatedAt == null) updatedAt = now;
		if (sagaStatus == null) sagaStatus = SagaStatus.STARTED;
		if (currentStep == null) currentStep = 1;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	public void advanceToStep(int nextStep) {
		this.currentStep = nextStep;
		this.sagaStatus = SagaStatus.PROCESSING;
	}

	public void complete() {
		this.sagaStatus = SagaStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
	}

	public void fail(String errorMessage) {
		this.sagaStatus = SagaStatus.FAILED;
		this.errorMessage = errorMessage;
		this.completedAt = LocalDateTime.now();
	}

	public void startCompensation() {
		this.sagaStatus = SagaStatus.COMPENSATING;
	}

	public void compensated() {
		this.sagaStatus = SagaStatus.COMPENSATING;
		this.completedAt = LocalDateTime.now();
	}

	public boolean isTerminated() {
		return sagaStatus == SagaStatus.COMPLETED
				|| sagaStatus == SagaStatus.FAILED
				|| sagaStatus == SagaStatus.COMPENSATING;
	}
// TODO: Need handle against
//	public void addStepLog(SagaStepLog stepLog) {
//		stepLogs.add(stepLog);
//		stepLog.setSagaInstance(this);
//	}

}
