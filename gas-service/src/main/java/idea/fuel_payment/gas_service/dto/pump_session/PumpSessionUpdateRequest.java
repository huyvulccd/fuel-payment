package idea.fuel_payment.gas_service.dto.pump_session;

import idea.fuel_payment.gas_service.domain.enums.SessionStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Request cập nhật trạng thái phiên bơm.
 *
 * @param sessionStatus trạng thái mới
 * @param completedAt thời gian hoàn thành (tuỳ chọn)
 * @author gas-service
 * @version 2026/04/03
 */
public record PumpSessionUpdateRequest(
        @NotNull(message = "sessionStatus is required")
        SessionStatus sessionStatus,

        LocalDateTime completedAt
) {
}
