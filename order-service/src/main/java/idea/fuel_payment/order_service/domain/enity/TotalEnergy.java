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
@Table(name = "total_energy")
public class TotalEnergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private Owner owner;

    @Column(name = "ron95", precision = 15, scale = 3)
    private BigDecimal ron95 = BigDecimal.ZERO;

    @Column(name = "e5", precision = 15, scale = 3)
    private BigDecimal e5 = BigDecimal.ZERO;

    @Column(name = "diesel", precision = 15, scale = 3)
    private BigDecimal diesel = BigDecimal.ZERO;

    @Column(name = "electronic", precision = 15, scale = 3)
    private BigDecimal electronic = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
