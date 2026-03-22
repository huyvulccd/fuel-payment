package idea.fuel_payment.order_service.dto.vehicle;

import idea.fuel_payment.order_service.validation.ValidatorUnique;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VehicleRegistrationRequest(
        @NotBlank
        @Size(max = 20)
        @ValidatorUnique(fieldName = "licensePlate", message = "License plate already exists")
        String licensePlate,

        @NotBlank
        Long ownerId,

        @NotBlank
        String nameVehicleModel
) {
}
