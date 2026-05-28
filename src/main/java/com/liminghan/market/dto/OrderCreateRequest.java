package com.liminghan.market.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateRequest {

    @NotNull(message = "goodsId cannot be null")
    private Long goodsId;
}
