package idea.fuel_payment.order_service.dto.owner;

import idea.fuel_payment.order_service.validation.ValidatorEmail;
import idea.fuel_payment.order_service.validation.ValidatorName;
import idea.fuel_payment.order_service.validation.ValidatorPhoneVietnam;
import idea.fuel_payment.order_service.validation.ValidatorUnique;

public record OwnerUpdateRequest(
        @ValidatorName
        String name,

        @ValidatorPhoneVietnam
        @ValidatorUnique(fieldName = "phone", message = "Phone number already exists")
        String phone,

        @ValidatorEmail
        @ValidatorUnique(fieldName = "email", message = "Email already exists")
        String email,

        String address
) {
}
