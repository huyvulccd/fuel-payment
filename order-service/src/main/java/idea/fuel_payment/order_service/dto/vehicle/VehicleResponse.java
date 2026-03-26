package idea.fuel_payment.order_service.dto.vehicle;

import idea.fuel_payment.order_service.domain.enity.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        String licensePlate,
        String ownerName,
        String idCard,
        BigDecimal balance,
        BigDecimal minBalance,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
	public static VehicleResponse notFound(String licensePlate) {
		return new VehicleResponse(
				null,
				licensePlate,
				null,
				null,
				null,
				null,
				"NOT_FOUND",
				null,
				null
		);
	}

	public static VehicleResponse from(Vehicle vehicle) {
		if (vehicle == null) return null;
		return new VehicleResponse(
				vehicle.getId(),
				vehicle.getLicensePlate(),
				vehicle.getOwner() != null ? vehicle.getOwner().getName() : null,
				vehicle.getOwner() != null ? vehicle.getOwner().getIdPersonal() : null,
				vehicle.getOwner() != null ? vehicle.getOwner().getBalance() : null,
				vehicle.getOwner() != null ? vehicle.getOwner().getMinBalance() : null,
				vehicle.getStatus() != null ? vehicle.getStatus().name() : null,
				vehicle.getCreatedAt(),
				vehicle.getUpdatedAt()
		);
	}
}
