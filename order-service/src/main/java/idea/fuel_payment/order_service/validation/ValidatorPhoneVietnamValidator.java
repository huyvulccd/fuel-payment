package idea.fuel_payment.order_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class ValidatorPhoneVietnamValidator implements ConstraintValidator<ValidatorPhoneVietnam, String> {
    
    private static final String PHONE_PATTERN = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
    private static final Pattern PATTERN = Pattern.compile(PHONE_PATTERN);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return PATTERN.matcher(value).matches();
    }
}
