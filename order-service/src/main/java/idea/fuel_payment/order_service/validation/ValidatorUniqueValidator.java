package idea.fuel_payment.order_service.validation;

import idea.fuel_payment.order_service.repository.OwnerRepository;
import idea.fuel_payment.order_service.repository.VehicleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorUniqueValidator implements ConstraintValidator<ValidatorUnique, String> {

    private final OwnerRepository ownerRepository;
    private final VehicleRepository vehicleRepository;
    private String fieldName;

    @Override
    public void initialize(ValidatorUnique constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Use @NotBlank or @NotNull for null checks
        }

        boolean exists = false;
        if ("email".equalsIgnoreCase(fieldName)) {
            exists = ownerRepository.existsByEmail(value);
        } else if ("phone".equalsIgnoreCase(fieldName)) {
            exists = ownerRepository.existsByPhone(value);
        } else if ("licensePlate".equalsIgnoreCase(fieldName)) {
            exists = vehicleRepository.existsByLicensePlate(value);
        }

        return !exists;
    }
}
