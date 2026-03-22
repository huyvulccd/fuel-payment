package idea.fuel_payment.order_service.dto.vehicle;

import idea.fuel_payment.order_service.domain.enity.VehicleModel;
import idea.fuel_payment.order_service.domain.enity.VehicleModel.VehicleType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record VehicleModelResponse(
        Long id,
        String name,
        VehicleType typeVehicle,
        BigDecimal capacityFuel
) {
    public static VehicleModelResponse from(VehicleModel model) {
        return VehicleModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .typeVehicle(model.getTypeVehicle())
                .capacityFuel(model.getCapacityFuel())
                .build();
    }
}
