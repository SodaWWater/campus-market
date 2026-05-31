package com.liminghan.market.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "orderId cannot be null")
    private Long orderId;
}
