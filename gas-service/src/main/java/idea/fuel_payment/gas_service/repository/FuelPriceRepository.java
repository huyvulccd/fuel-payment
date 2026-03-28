package idea.fuel_payment.gas_service.repository;

import idea.fuel_payment.gas_service.domain.entity.FuelPrice;
import idea.fuel_payment.gas_service.domain.entity.FuelType;
import idea.fuel_payment.gas_service.dto.query.FuelPricePro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FuelPriceRepository extends JpaRepository<FuelPrice, Long> {

    List<FuelPricePro> findByIsCurrentTrue();

    List<FuelPrice> findByFuelTypeInAndIsCurrentTrue(Set<FuelType> fuelTypes);

    @Modifying
    @Query("UPDATE FuelPrice f SET f.isCurrent = false WHERE f.fuelType IN :fuelTypes AND f.isCurrent = true")
    void deactivateCurrentByFuelType(@Param("fuelTypes") Set<FuelType> fuelTypes);
}
