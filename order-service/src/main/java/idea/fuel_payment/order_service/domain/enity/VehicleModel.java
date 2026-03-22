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
@Table(name = "vehicle_model")
public class VehicleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_vehicle", nullable = false)
    private VehicleType typeVehicle;

    @Column(name = "fuel_type", length = 20)
    private String fuelType;

    // Fuel tank capacity in liters, or battery capacity in kWh
    @Column(name = "capacity_fuel", precision = 10, scale = 2)
    private BigDecimal capacityFuel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum VehicleType {
        BIKE_110CC,
        BIKE_125CC,
        BIKE_150CC,
        ELECTRIC_BIKE,
        CAR_SEDAN,
        CAR_SUV,
        ELECTRIC_CAR,
        TRUCK,
        BUS
    }
}
