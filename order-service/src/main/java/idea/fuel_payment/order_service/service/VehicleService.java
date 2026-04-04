package idea.fuel_payment.order_service.service;

import idea.fuel_payment.order_service.domain.common.RedisTool;
import idea.fuel_payment.order_service.domain.common.UtilityService;
import idea.fuel_payment.order_service.domain.enity.Owner;
import idea.fuel_payment.order_service.domain.enity.TotalEnergy;
import idea.fuel_payment.order_service.domain.enity.Vehicle;
import idea.fuel_payment.order_service.domain.enity.VehicleModel;
import idea.fuel_payment.order_service.dto.vehicle.*;
import idea.fuel_payment.order_service.repository.OwnerRepository;
import idea.fuel_payment.order_service.repository.TotalEnergyRepository;
import idea.fuel_payment.order_service.repository.VehicleModelRepository;
import idea.fuel_payment.order_service.repository.VehicleRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
    public class VehicleService extends UtilityService {

    private final VehicleRepository vehicleRepository;

    private final VehicleModelRepository vehicleModelRepository;

    private final OwnerRepository ownerRepository;

    private final TotalEnergyRepository totalEnergyRepository;

    private final RedisTool redisTool;

    public List<VehicleResponse> getVehiclesByOwnerId(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId).stream()
                .map(VehicleResponse::from)
                .toList();
    }

    public List<VehicleModelResponse> getAllVehicleModels() {
        return vehicleModelRepository.findAll().stream()
                .map(VehicleModelResponse::from)
                .toList();
    }

    public VehicleResponse getVehicleByLicensePlate(String licensePlate) {
        Optional<Vehicle> otpLicensePlate = vehicleRepository.findOneByLicensePlate(licensePlate);

	    return otpLicensePlate.map(VehicleResponse::from).orElseGet(() -> VehicleResponse.notFound(licensePlate));

    }

    @Transactional
    public VehicleRegistrationResponse registerVehicle(VehicleRegistrationRequest request) {
        if (vehicleRepository.existsByLicensePlate(request.licensePlate())) {
            return VehicleRegistrationResponse.error(List.of("License plate already exists"));
        }

        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new ValidationException("Owner not found with id: " + request.ownerId()));

        VehicleModel model = vehicleModelRepository.findByName(request.nameVehicleModel())
                .orElseThrow(() -> new ValidationException("Vehicle model not found: " + request.nameVehicleModel()));

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setOwner(owner);
        vehicle.setVehicleModel(model);

        vehicle = vehicleRepository.save(vehicle);

        // Update TotalEnergy
        TotalEnergy energy = totalEnergyRepository.findByOwnerId(owner.getId())
                .orElseGet(() -> {
                    TotalEnergy newEnergy = new TotalEnergy();
                    newEnergy.setOwner(owner);
                    // Initialize BigDecimals to ZERO to avoid NullPointerExceptions
                    newEnergy.setRon95(BigDecimal.ZERO);
                    newEnergy.setE5(BigDecimal.ZERO);
                    newEnergy.setDiesel(BigDecimal.ZERO);
                    newEnergy.setElectronic(BigDecimal.ZERO);
                    return newEnergy;
                });

        BigDecimal capacity = model.getCapacityFuel() != null ? model.getCapacityFuel() : BigDecimal.ZERO;
        String fuelType = model.getFuelType();
        
        if ("RON95".equalsIgnoreCase(fuelType)) {
            energy.setRon95(energy.getRon95().add(capacity));
        } else if ("E5".equalsIgnoreCase(fuelType)) {
            energy.setE5(energy.getE5().add(capacity));
        } else if ("DIESEL".equalsIgnoreCase(fuelType)) {
            energy.setDiesel(energy.getDiesel().add(capacity));
        } else if ("ELECTRONIC".equalsIgnoreCase(fuelType)) {
            energy.setElectronic(energy.getElectronic().add(capacity));
        }
        totalEnergyRepository.save(energy);

        // Calculate minBalance adjustment (only for the new vehicle)
        BigDecimal currentPrice = getValue(redisTool.hGet(RedisTool.CURRENT_FUEL_PRICE, fuelType), BigDecimal.class);
        BigDecimal minBalanceToAdd = capacity.multiply(currentPrice);

        owner.setMinBalance(owner.getMinBalance().add(minBalanceToAdd));
        ownerRepository.save(owner);

        return VehicleRegistrationResponse.success(vehicle, owner.getBalance(), owner.getMinBalance());
    }

    public void updateVehicle(String licensePlate, VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findOneByLicensePlate(licensePlate)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + licensePlate));

        if (request.ownerId() != null) {
            Owner newOwner = ownerRepository.findById(request.ownerId())
                    .orElseThrow(() -> new RuntimeException("Owner not found: " + request.ownerId()));
            vehicle.setOwner(newOwner);
        }

        vehicleRepository.save(vehicle);
    }

}
