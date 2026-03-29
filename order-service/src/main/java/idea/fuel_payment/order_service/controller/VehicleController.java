package idea.fuel_payment.order_service.controller;

import idea.fuel_payment.order_service.domain.constanst.Message;
import idea.fuel_payment.order_service.dto.vehicle.*;
import idea.fuel_payment.order_service.service.VehicleService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    
    @GetMapping
    public List<VehicleResponse> getVehiclesByOwnerId(@RequestParam("ownerId") Long ownerId) {
        return vehicleService.getVehiclesByOwnerId(ownerId);
    }

    @GetMapping("/{licensePlate}")
    public VehicleResponse getVehicleByLicensePlate(@PathVariable String licensePlate) {
        return vehicleService.getVehicleByLicensePlate(licensePlate);
    }

    @PostMapping("/register")
    public VehicleRegistrationResponse registerVehicle(@RequestBody @Valid VehicleRegistrationRequest request) {
        try {
            return vehicleService.registerVehicle(request);
        } catch (ValidationException e) {
            return VehicleRegistrationResponse.error(List.of(e.getMessage()));
        } catch (Exception e) {
            return VehicleRegistrationResponse.error(List.of(Message.PLS_CALL_US));
        }
    }

    @PutMapping("/{licensePlate}")
    public VehicleResponse updateVehicle(@PathVariable String licensePlate, @RequestBody @Valid VehicleUpdateRequest request) {
        vehicleService.updateVehicle(licensePlate, request);
        return vehicleService.getVehicleByLicensePlate(licensePlate);
    }

    @GetMapping("/models")
    public List<VehicleModelResponse> getAllVehicleModels() {
        return vehicleService.getAllVehicleModels();
    }
}
