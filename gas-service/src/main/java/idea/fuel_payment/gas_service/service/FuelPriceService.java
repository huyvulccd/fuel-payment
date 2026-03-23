package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.common.RedisTool;
import idea.fuel_payment.gas_service.config.RedisConfig;
import idea.fuel_payment.gas_service.domain.dto_protected.FuelPriceDto;
import idea.fuel_payment.gas_service.domain.entity.FuelPrice;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPriceResponse;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPriceUpdateRequest;
import idea.fuel_payment.gas_service.repository.FuelPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuelPriceService {

    // Redis price-only key (create for order-service search price O(1))
    // key: fuel:price:{FUEL_TYPE}  (e.g. fuel:price:RON95)

    private final FuelPriceRepository fuelPriceRepository;
    private final RedisTool redisTool;

    // ───────── READ ─────────

    @Cacheable(cacheNames = RedisConfig.CACHE_FUEL_PRICES, key = "'all-current'")
    @Transactional(readOnly = true)
    public List<FuelPriceDto> getAllCurrentPrices() {
        log.debug("[Cache] MISS fuel-prices::all-current — querying DB");
        return fuelPriceRepository.findByIsCurrentTrue();
    }

//    @CacheEvict(cacheNames = RedisConfig.CACHE_FUEL_PRICES, allEntries = true)
    // delete all cache related to CACHE_FUEL_PRICES
    @Transactional
    public FuelPriceResponse updatePrice(Long id, FuelPriceUpdateRequest request) {
        FuelPrice existing = fuelPriceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FuelPrice not found: " + id));

        FuelPrice.FuelType fuelType = existing.getFuelType();

        // 1. Deactivate all current records for this fuel type
        fuelPriceRepository.deactivateCurrentByFuelType(fuelType);

        // 2. Insert new current price record
        FuelPrice newPrice = new FuelPrice();
        newPrice.setFuelType(fuelType);
        newPrice.setPrice(request.getPrice());
        newPrice.setEffectiveFrom(request.getEffectiveFrom());
        newPrice.setIsCurrent(true);

        FuelPrice saved = fuelPriceRepository.save(newPrice);

        // 3. Write-through price-only key → order-service dùng key này tra nhanh O(1)
        String priceKey = RedisConfig.CACHE_FUEL_PRICES;
        Optional<String> optPrice = redisTool.get(priceKey);

        if (optPrice.isPresent()) {
            Map<String, Object> prices = RedisTool.toMap(optPrice.get());
            prices.put(fuelType.name(), newPrice);
            redisTool.set(RedisConfig.CACHE_FUEL_PRICES, prices);
        }
        redisTool.set(priceKey, request.getPrice().toPlainString());
        log.debug("[Cache] Evicted fuel-prices cache. Updated price key={}", priceKey);

        return toResponse(saved);
    }

    // ───────── MAPPER ─────────

    private FuelPriceResponse toResponse(FuelPrice fp) {
        return FuelPriceResponse.builder()
                .id(fp.getId())
                .fuelType(fp.getFuelType())
                .price(fp.getPrice())
                .effectiveFrom(fp.getEffectiveFrom())
                .isCurrent(fp.getIsCurrent())
                .createdAt(fp.getCreatedAt())
                .build();
    }
}
