package idea.fuel_payment.order_service.dto.fuel_order;

public record FuelOrderResponse(
        String orderCode,
        String licensePlate,
        Long stationId,
        String status
) {
}
