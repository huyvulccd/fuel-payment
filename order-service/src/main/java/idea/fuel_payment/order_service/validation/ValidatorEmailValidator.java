package idea.fuel_payment.order_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidatorEmailValidator implements ConstraintValidator<ValidatorEmail, String> {

    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return PATTERN.matcher(value).matches();
    }
}
