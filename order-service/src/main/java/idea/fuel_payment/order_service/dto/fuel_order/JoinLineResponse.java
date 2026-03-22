package idea.fuel_payment.order_service.dto.fuel_order;

public record JoinLineResponse(
        String orderCode,
        String licensePlate,
        Long stationId,
        Integer queuePosition,
        Long fuelPumpId
) {
}
