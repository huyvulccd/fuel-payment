package idea.fuel_payment.order_service.dto.owner;

import java.util.List;

public record OwnerResponse(
        Long id,
        String name,
        String phone,
        String email,
        List<String> messages
) {
}
