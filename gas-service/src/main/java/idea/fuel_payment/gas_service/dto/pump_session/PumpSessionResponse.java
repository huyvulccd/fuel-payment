package idea.fuel_payment.gas_service.dto.pump_session;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.domain.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pump session information response.
 *
 * @param sessionCode session code
 * @param pumpId fuel pump ID
 * @param orderCode order code
 * @param licensePlate license plate
 * @param fuelType fuel type
 * @param quantityLiters liters pumped
 * @param unitPrice unit price
 * @param totalAmount total amount
 * @param sessionStatus session status
 * @param startedAt start time
 * @param completedAt completion time
 * @author gas-service
 * @version 2026/04/03
 */
public record PumpSessionResponse(
        String sessionCode,
        Long pumpId,
        String orderCode,
        String licensePlate,
        FuelType fuelType,
        BigDecimal quantityLiters,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        SessionStatus sessionStatus,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}
