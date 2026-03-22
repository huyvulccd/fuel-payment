package idea.fuel_payment.order_service.domain.enity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "fuel_order")
public class FuelOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_code", nullable = false, unique = true, length = 30)
	private String orderCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id", nullable = false)
	private Vehicle vehicle;

	@Column(name = "license_plate", nullable = false, length = 20)
	private String licensePlate;

	@Column(name = "station_id", nullable = false)
	private Long stationId;

	@Column(name = "pump_id", nullable = false)
	private Long pumpId;

	@Enumerated(EnumType.STRING)
	@Column(name = "fuel_type", nullable = false)
	private FuelType fuelType;

	@Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@Column(name = "quantity_liters", precision = 10, scale = 3)
	private BigDecimal quantityLiters = BigDecimal.ZERO;

	@Column(name = "total_amount", precision = 15, scale = 2)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false)
	private OrderStatus orderStatus = OrderStatus.CREATED;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// ============================
	// Enums
	// ============================
	public enum FuelType {
		RON95,
		E5,
		DIESEL
	}

	public enum OrderStatus {
		CREATED,
		PUMPING,
		PAYMENT_PENDING,
		COMPLETED,
		FAILED
	}

}
