package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.OrderCreateRequest;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create order")
    @PostMapping
    public Result<MarketOrder> create(@Valid @RequestBody OrderCreateRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    @Operation(summary = "List my orders")
    @GetMapping("/my")
    public Result<List<MarketOrder>> myOrders() {
        return Result.success(orderService.listMyOrders());
    }

    @Operation(summary = "Get order detail")
    @GetMapping("/{id}")
    public Result<MarketOrder> get(@PathVariable Long id) {
        return Result.success(orderService.getOrder(id));
    }

    @Operation(summary = "Pay order")
    @PutMapping("/{id}/pay")
    public Result<MarketOrder> pay(@PathVariable Long id) {
        return Result.success(orderService.payOrder(id));
    }

    @Operation(summary = "Cancel order")
    @PutMapping("/{id}/cancel")
    public Result<MarketOrder> cancel(@PathVariable Long id) {
        return Result.success(orderService.cancelOrder(id));
    }

    @Operation(summary = "Finish order")
    @PutMapping("/{id}/finish")
    public Result<MarketOrder> finish(@PathVariable Long id) {
        return Result.success(orderService.finishOrder(id));
    }
}
