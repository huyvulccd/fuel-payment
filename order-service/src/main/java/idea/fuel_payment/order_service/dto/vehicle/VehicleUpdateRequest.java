package idea.fuel_payment.order_service.dto.vehicle;

import jakarta.validation.constraints.NotNull;

public record VehicleUpdateRequest(
        @NotNull
        Long ownerId
) {
}
