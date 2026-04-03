package idea.fuel_payment.gas_service.dto.pump_session;

import idea.fuel_payment.gas_service.domain.enums.FuelType;
import idea.fuel_payment.gas_service.domain.enums.SessionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response thông tin phiên bơm xăng.
 *
 * @param sessionCode mã phiên bơm
 * @param pumpId ID trụ bơm
 * @param orderCode mã đơn hàng
 * @param licensePlate biển số xe
 * @param fuelType loại nhiên liệu
 * @param quantityLiters số lít bơm
 * @param unitPrice đơn giá
 * @param totalAmount tổng tiền
 * @param sessionStatus trạng thái phiên bơm
 * @param startedAt thời gian bắt đầu
 * @param completedAt thời gian hoàn thành
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
