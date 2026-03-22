package idea.fuel_payment.order_service.dto.vehicle;

public record VehicleStatusUpdateRequest(
        String status,
        String reason
) {
}
