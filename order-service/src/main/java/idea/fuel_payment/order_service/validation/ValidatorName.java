package idea.fuel_payment.order_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidatorNameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatorName {
    String message() default "Name must be between 6 and 100 characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
