package com.liminghan.market.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectGoodsRequest {

    @NotBlank(message = "reason cannot be blank")
    private String reason;
}
