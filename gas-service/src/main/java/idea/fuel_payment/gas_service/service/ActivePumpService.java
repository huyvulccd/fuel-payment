package idea.fuel_payment.gas_service.service;

import idea.fuel_payment.gas_service.domain.entity.FuelInventory;
import idea.fuel_payment.gas_service.domain.entity.FuelPump;
import idea.fuel_payment.gas_service.repository.FuelInventoryRepository;
import idea.fuel_payment.gas_service.repository.FuelPumpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivePumpService {

	private final FuelInventoryRepository fuelInventoryRepository;
	private final FuelPumpRepository fuelPumpRepository;

	public Map<String, Object> activePump(final Long pumpId) {

		boolean isEnoughFuel = isEnough(pumpId);
	}

	private boolean isEnough(Long pumpId) {
		FuelPump pump = fuelPumpRepository.findByPumpId(pumpId);
		Long stationId = pump.getStationId();

		Optional<FuelInventory> inventoryFuelType = fuelInventoryRepository.
				findByStationIdAndFuelType(stationId, pump.getFuelType());

	}
}
