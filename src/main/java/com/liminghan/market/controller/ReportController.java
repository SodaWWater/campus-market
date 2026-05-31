package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.SubmitReportRequest;
import com.liminghan.market.entity.MarketReport;
import com.liminghan.market.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Submit report")
    @PostMapping
    public Result<MarketReport> submit(@Valid @RequestBody SubmitReportRequest request) {
        return Result.success(reportService.submitReport(
                request.getTargetType(), request.getTargetId(),
                request.getReason(), request.getDescription()));
    }

    @Operation(summary = "List my reports")
    @GetMapping("/my")
    public Result<List<MarketReport>> myReports() {
        return Result.success(reportService.listMyReports());
    }
}
