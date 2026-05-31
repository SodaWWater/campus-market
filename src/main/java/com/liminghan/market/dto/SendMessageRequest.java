package com.liminghan.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotNull(message = "receiverId cannot be null")
    private Long receiverId;

    private Long goodsId;

    private Long orderId;

    @NotBlank(message = "content cannot be blank")
    private String content;
}
