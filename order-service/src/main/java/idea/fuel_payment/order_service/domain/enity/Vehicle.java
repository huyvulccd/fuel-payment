package idea.fuel_payment.order_service.domain.enity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "vehicle")
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "license_plate", nullable = false, unique = true, length = 20)
	private String licensePlate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private Owner owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vehicle_model", nullable = false)
	private VehicleModel vehicleModel;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public Owner.OwnerStatus getStatus() {
		return owner != null ? owner.getStatus() : null;
	}
}
