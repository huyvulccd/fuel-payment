package idea.fuel_payment.order_service.dto.vehicle;

import idea.fuel_payment.order_service.domain.enity.Vehicle;

import java.math.BigDecimal;
import java.util.List;

public record VehicleRegistrationResponse(
        Long id,
        String licensePlate,
        BigDecimal balance,
        BigDecimal minBalance,
        String status,
        List<String> message
){

	public static VehicleRegistrationResponse error(List<String> messages) {
		return new VehicleRegistrationResponse(null,
				null,
				null,
				null,
				messages
				);
	}

	public static VehicleRegistrationResponse success(Vehicle vehicle, BigDecimal balance, BigDecimal minBalance) {
		return new VehicleRegistrationResponse(vehicle.getId(),
				vehicle.getLicensePlate(),
				balance,
				minBalance,
				vehicle.getStatus().name(),
				null
		);
	}
}
