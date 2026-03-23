package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.dto_protected.FuelPriceDto;
import idea.fuel_payment.gas_service.domain.entity.FuelPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelPriceRepository extends JpaRepository<FuelPrice, Long> {

    List<FuelPriceDto> findByIsCurrentTrue();

    Optional<FuelPrice> findByFuelTypeAndIsCurrentTrue(FuelPrice.FuelType fuelType);

    @Modifying
    @Query("UPDATE FuelPrice f SET f.isCurrent = false WHERE f.fuelType = :fuelType AND f.isCurrent = true")
    void deactivateCurrentByFuelType(@Param("fuelType") FuelPrice.FuelType fuelType);
}
