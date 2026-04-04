package idea.fuel_payment.order_service.controller;

import idea.fuel_payment.order_service.dto.fuel_order.FuelOrderResponse;
import idea.fuel_payment.order_service.dto.fuel_order.FuelOrderUpdateRequest;
import idea.fuel_payment.order_service.dto.fuel_order.JoinLineRequest;
import idea.fuel_payment.order_service.dto.fuel_order.JoinLineResponse;
import idea.fuel_payment.order_service.service.FuelOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fuel-orders")
@RequiredArgsConstructor
public class FuelOrderController {

    private final FuelOrderService fuelOrderService;

    @GetMapping
    public List<FuelOrderResponse> getFuelOrdersByOwnerId(@RequestParam("ownerId") Long ownerId) {
        return fuelOrderService.getFuelOrdersByOwnerId(ownerId);
    }

    @GetMapping("/{orderCode}")
    public FuelOrderResponse getFuelOrder(@PathVariable String orderCode) {
        return fuelOrderService.getFuelOrder(orderCode);
    }

    @PostMapping("/join-line")
    public JoinLineResponse joinLine(@RequestBody JoinLineRequest request) {
        return fuelOrderService.joinLine(request);
    }

    @PutMapping("/{orderCode}")
    public FuelOrderResponse updateFuelOrder(@PathVariable String orderCode, @RequestBody FuelOrderUpdateRequest request) {
        return fuelOrderService.updateFuelOrder(orderCode, request);
    }
}
