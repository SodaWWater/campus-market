package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.PaymentRequest;
import com.liminghan.market.entity.PaymentRecord;
import com.liminghan.market.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Mock pay order")
    @PostMapping("/mock-pay")
    public Result<PaymentRecord> mockPay(@Valid @RequestBody PaymentRequest request) {
        return Result.success(paymentService.mockPay(request.getOrderId()));
    }

    @Operation(summary = "Get order payment")
    @GetMapping("/order/{orderId}")
    public Result<PaymentRecord> getByOrder(@PathVariable Long orderId) {
        return Result.success(paymentService.getByOrderId(orderId));
    }
}
