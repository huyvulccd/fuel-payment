package idea.fuel_payment.order_service.service;

import idea.fuel_payment.order_service.domain.common.UtilityService;
import idea.fuel_payment.order_service.domain.enity.Owner;
import idea.fuel_payment.order_service.dto.owner.OwnerRegistrationRequest;
import idea.fuel_payment.order_service.dto.owner.OwnerResponse;
import idea.fuel_payment.order_service.dto.owner.OwnerUpdateRequest;
import idea.fuel_payment.order_service.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerService extends UtilityService {

    private final OwnerRepository ownerRepository;

    public OwnerResponse registerOwner(OwnerRegistrationRequest request) {
        Owner owner = new Owner();
        owner.setName(request.name());
        owner.setPhone(request.phone());
        owner.setEmail(request.email());
        owner.setIdPersonal(request.idPersonal());
        owner.setStatus(Owner.OwnerStatus.ACTIVE);
        owner.setBalance(java.math.BigDecimal.ZERO);

        owner = ownerRepository.save(owner);

        return mapToOwnerResponse(owner, java.util.List.of("Register owner success"));
    }

    public OwnerResponse getOwnerById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found")); // Simple exception for now
        return mapToOwnerResponse(owner, java.util.List.of("Get owner success"));
    }

    public OwnerResponse updateOwner(Long id, OwnerUpdateRequest request) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (isNotEmpty(request.name())) {
            owner.setName(request.name());
        }
        if (isNotEmpty(request.phone())) {
            owner.setPhone(request.phone());
        }
        if (isNotEmpty(request.email())) {
            owner.setEmail(request.email());
        }
        if (isNotEmpty(request.address())) {
            owner.setAddress(request.address());
        }

        owner = ownerRepository.save(owner);
        return mapToOwnerResponse(owner, List.of("Update owner success"));
    }

    private OwnerResponse mapToOwnerResponse(Owner owner, List<String> messages) {
        return new OwnerResponse(
                owner.getId(),
                owner.getName(),
                owner.getPhone(),
                owner.getEmail(),
                messages
        );
    }
}
