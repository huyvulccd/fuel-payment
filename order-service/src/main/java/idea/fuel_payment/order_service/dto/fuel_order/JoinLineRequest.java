package idea.fuel_payment.order_service.dto.fuel_order;

public record JoinLineRequest(
        String licensePlate,
        Long stationId
) {
}
