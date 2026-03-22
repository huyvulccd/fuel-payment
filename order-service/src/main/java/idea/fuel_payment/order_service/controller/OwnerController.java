package idea.fuel_payment.order_service.controller;

import idea.fuel_payment.order_service.dto.owner.OwnerRegistrationRequest;
import idea.fuel_payment.order_service.dto.owner.OwnerResponse;
import idea.fuel_payment.order_service.dto.owner.OwnerUpdateRequest;
import idea.fuel_payment.order_service.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping("/register")
    public OwnerResponse registerOwner(@RequestBody @Valid OwnerRegistrationRequest request) {
        return ownerService.registerOwner(request);
    }

    @GetMapping("/{id}")
    public OwnerResponse getOwnerById(@PathVariable Long id) {
        return ownerService.getOwnerById(id);
    }

    @PutMapping("/{id}")
    public OwnerResponse updateOwner(@PathVariable Long id, @RequestBody @Valid OwnerUpdateRequest request) {
        return ownerService.updateOwner(id, request);
    }
}
