package com.liminghan.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitReportRequest {

    @NotBlank(message = "targetType cannot be blank")
    private String targetType;

    @NotNull(message = "targetId cannot be null")
    private Long targetId;

    @NotBlank(message = "reason cannot be blank")
    private String reason;

    private String description;
}
