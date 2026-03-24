package idea.fuel_payment.orchestrator_service.domain.entity;

import idea.fuel_payment.orchestrator_service.domain.enums.StepName;
import idea.fuel_payment.orchestrator_service.domain.enums.StepStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_step_logs",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_saga_step", columnNames = {"saga_id", "step_order"})
		},
		indexes = {
				@Index(name = "idx_step_saga", columnList = "saga_id")
		})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStepLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "saga_id", nullable = false, length = 50)
	private String sagaId;

	@Column(name = "step_order", nullable = false)
	private Integer stepOrder;

	@Enumerated(EnumType.STRING)
	@Column(name = "step_name", nullable = false, length = 100)
	private StepName stepName;

	@Column(name = "service_name", nullable = false, length = 50)
	private String serviceName;

	@Enumerated(EnumType.STRING)
	@Column(name = "step_status")
	private StepStatus stepStatus;

	@Column(name = "request_payload", length = 10_000)
	private String requestPayload;

	@Column(name = "response_payload", length = 10_000)
	private String responsePayload;

	@Column(name = "error_message", length = 500)
	private String errorMessage;

	private LocalDateTime startedAt;
	private LocalDateTime completedAt;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;
}
