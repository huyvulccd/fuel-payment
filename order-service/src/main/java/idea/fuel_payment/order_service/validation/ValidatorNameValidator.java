package idea.fuel_payment.order_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidatorNameValidator implements ConstraintValidator<ValidatorName, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return value.length() >= 6 && value.length() <= 100;
    }
}
