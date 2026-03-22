package idea.fuel_payment.order_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidatorPhoneVietnamValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatorPhoneVietnam {
    String message() default "Invalid Vietnam phone number format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
