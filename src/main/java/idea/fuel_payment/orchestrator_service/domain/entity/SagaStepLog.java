package idea.fuel_payment.orchestrator_service.domain.entity;

import idea.fuel_payment.orchestrator_service.domain.enums.StepAction;
import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import idea.fuel_payment.orchestrator_service.domain.enums.StepStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStepLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "saga_id", nullable = false, length = 50,
			insertable = false, updatable = false)
	private String sagaId;

	@Column(name = "step_order", nullable = false)
	private Integer stepOrder;

	@Enumerated(EnumType.STRING)
	@Column(name = "step_name", nullable = false, length = 50)
	private StepName stepName;

	@Enumerated(EnumType.STRING)
	@Column(name = "step_action", nullable = false, length = 50)
	private StepAction stepAction;

	@Enumerated(EnumType.STRING)
	@Column(name = "step_status", nullable = false, length = 20)
	private StepStatus stepStatus;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "request_payload", columnDefinition = "jsonb")
	private Map<String, Object> requestPayload;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "response_payload", columnDefinition = "jsonb")
	private Map<String, Object> responsePayload;

	@Column(name = "error_message", columnDefinition = "TEXT")
	private String errorMessage;

	@Column(name = "retry_count", nullable = false)
	@Builder.Default
	private Integer retryCount = 0;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	// Relationship
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "saga_id", referencedColumnName = "saga_id")
	private SagaInstance sagaInstance;

	@PrePersist
	protected void onCreate() {
		if (createdAt == null) createdAt = LocalDateTime.now();
		if (stepStatus == null) stepStatus = StepStatus.PENDING;
		if (retryCount == null) retryCount = 0;
	}

	// Domain Methods
	public void markInProgress() {
		this.stepStatus = StepStatus.IN_PROGRESS;
		this.startedAt = LocalDateTime.now();
	}

	public void markSuccess(Map<String, Object> responsePayload) {
		this.stepStatus = StepStatus.SUCCESS;
		this.responsePayload = responsePayload;
		this.completedAt = LocalDateTime.now();
	}

	public void markFailed(String errorMessage) {
		this.stepStatus = StepStatus.FAILED;
		this.errorMessage = errorMessage;
		this.completedAt = LocalDateTime.now();
	}

	public void incrementRetry() {
		this.retryCount++;
	}
}