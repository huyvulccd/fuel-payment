package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.common.RedisTool;
import idea.fuel_payment.gas_service.domain.common.UtilityService;
import idea.fuel_payment.gas_service.config.RedisConfig;
import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPriceUpdateRequest;
import idea.fuel_payment.gas_service.dto.query.FuelPricePro;
import idea.fuel_payment.gas_service.domain.entity.FuelPrice;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPriceResponse;
import idea.fuel_payment.gas_service.dto.fuel_price.FuelPricesUpdateRequest;
import idea.fuel_payment.gas_service.repository.FuelPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuelPriceService extends UtilityService {

	// Redis price-only key (create for order-service search price O(1))
	// key: fuel:price:{FUEL_TYPE}  (e.g. fuel:price:RON95)

	private final FuelPriceRepository fuelPriceRepository;
	private final RedisTool redisTool;

	// ───────── READ ─────────

//	@Cacheable(cacheNames = RedisConfig.CACHE_FUEL_PRICES, key = "'all-current'")
	@Transactional(readOnly = true)
	public Map<String, BigDecimal> getAllCurrentPrices() {
		String key = RedisTool.concatKey(RedisConfig.CACHE_FUEL_PRICES, "all");
		Map<String, BigDecimal> cache = redisTool.hGet(key, BigDecimal.class);
		if (isNotEmpty(cache)) {
			return cache;
		}

		log.debug("[Cache] MISS fuel-prices::all-current — querying DB");
		List<FuelPricePro> byIsCurrentTrue = fuelPriceRepository.findByIsCurrentTrue();
		Map<String, BigDecimal> collect = byIsCurrentTrue.stream().collect(Collectors.toMap(
				e -> e.getFuelType().name(),
				FuelPricePro::getPrice
		));
		redisTool.hSet(key, collect);
		return collect;
	}

	// @CacheEvict(cacheNames = RedisConfig.CACHE_FUEL_PRICES, allEntries = true)
	// delete all cache related to CACHE_FUEL_PRICES
	@Transactional
	public List<FuelPriceResponse> updatePrice(FuelPricesUpdateRequest request) {
		List<FuelPriceUpdateRequest> prices = request.getPrices();
		LocalDateTime effectiveFrom = request.getEffectiveFrom();
		LocalDateTime now = LocalDateTime.now();

		Set<FuelType> typeToCheck = prices.stream().map(FuelPriceUpdateRequest::getFuelType)
				.collect(Collectors.toSet());

		List<FuelPrice> existingFuels = fuelPriceRepository.findByFuelTypeInAndIsCurrentTrue(typeToCheck);
		log.info("params: {}\nfound: {}", typeToCheck, existingFuels);


		List<FuelPriceUpdateRequest> fuelsToUpdate = new ArrayList<>();

		for (FuelPriceUpdateRequest price: prices) {
			Optional<FuelPrice> toUpdate = existingFuels.stream()
					.filter(e -> e.getFuelType().compareTo(price.getFuelType()) == 0)
					.findFirst();
			if (toUpdate.isPresent()) {
				if (toUpdate.get().getPrice().compareTo(price.getPrice()) != 0) {
					fuelsToUpdate.add(price);
				}
			} else {
				fuelsToUpdate.add(price);
			}
		}
		if (isEmpty(fuelsToUpdate)) {
			log.debug("There are no fuel to update price");
			return Collections.emptyList();
		}

		List<FuelPrice> updates = new ArrayList<>();

		Set<FuelType> typeToInactive = fuelsToUpdate.stream().map(FuelPriceUpdateRequest::getFuelType).collect(Collectors.toSet());

		fuelPriceRepository.deactivateCurrentByFuelType(typeToInactive);

		// 2. Insert new current price record
        for (FuelPriceUpdateRequest price: fuelsToUpdate) {
	        FuelPrice newPrice = new FuelPrice();
	        newPrice.setFuelType(price.getFuelType());
	        newPrice.setPrice(price.getPrice());
	        newPrice.setEffectiveFrom(getValue(price.getEffectiveFrom(), effectiveFrom, now));
	        newPrice.setIsCurrent(true);

			updates.add(newPrice);
        }


        List<FuelPrice> fuelsSaved = fuelPriceRepository.saveAll(updates);

        // 3. Write-through hash key used by getAllCurrentPrices(): fuel-prices::all
        String allCurrentKey = RedisTool.concatKey(RedisConfig.CACHE_FUEL_PRICES, "all");

        Map<String, BigDecimal> currentCache = redisTool.hGet(allCurrentKey, BigDecimal.class);
        if (isNotEmpty(currentCache)) {
	        Map<String, BigDecimal> patch = fuelsSaved.stream().collect(Collectors.toMap(
			        e -> e.getFuelType().name(),
			        FuelPrice::getPrice
	        ));
	        redisTool.hSet(allCurrentKey, patch);
	        log.debug("[Cache] Patched fuel-prices hash key={} fields={}", allCurrentKey, patch.keySet());
        } else {
	        List<FuelPricePro> allCurrent = fuelPriceRepository.findByIsCurrentTrue();
	        Map<String, BigDecimal> allCurrentMap = allCurrent.stream().collect(Collectors.toMap(
			        e -> e.getFuelType().name(),
			        FuelPricePro::getPrice
	        ));
	        redisTool.hSet(allCurrentKey, allCurrentMap);
	        log.debug("[Cache] Rebuilt fuel-prices hash key={}", allCurrentKey);
        }

		return toResponse(fuelsSaved);
	}

	// ───────── MAPPER ─────────

	private List<FuelPriceResponse> toResponse(List<FuelPrice> fuelsPrice) {
		return fuelsPrice.stream()
				.map(fuelPrice -> FuelPriceResponse.builder()
						.id(fuelPrice.getId())
						.fuelType(fuelPrice.getFuelType())
						.price(fuelPrice.getPrice())
						.effectiveFrom(fuelPrice.getEffectiveFrom())
						.isCurrent(fuelPrice.getIsCurrent())
						.createdAt(fuelPrice.getCreatedAt())
						.build())
				.toList();
	}
}
