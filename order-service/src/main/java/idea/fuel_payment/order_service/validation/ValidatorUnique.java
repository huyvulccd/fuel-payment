package idea.fuel_payment.order_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidatorUniqueValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatorUnique {
    String message() default "Value must be unique";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String fieldName();
}
