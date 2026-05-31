package com.liminghan.market.service;

import com.liminghan.market.entity.MarketReport;

import java.util.List;

public interface ReportService {

    MarketReport submitReport(String targetType, Long targetId, String reason, String description);

    List<MarketReport> listMyReports();

    List<MarketReport> listAllReports();

    MarketReport handleReport(Long id, String status, String handleResult);
}
