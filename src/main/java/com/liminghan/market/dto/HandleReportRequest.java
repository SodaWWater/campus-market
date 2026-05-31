package com.liminghan.market.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HandleReportRequest {

    @NotBlank(message = "status cannot be blank")
    private String status;

    @NotBlank(message = "handleResult cannot be blank")
    private String handleResult;
}
