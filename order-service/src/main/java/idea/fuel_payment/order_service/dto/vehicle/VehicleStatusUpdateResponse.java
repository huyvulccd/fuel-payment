package idea.fuel_payment.order_service.dto.vehicle;

public record VehicleStatusUpdateResponse(
        String licensePlate,
        String status,
        String message
) {
}
