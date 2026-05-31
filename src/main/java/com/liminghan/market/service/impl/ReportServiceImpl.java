package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.entity.MarketReport;
import com.liminghan.market.mapper.MarketReportMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl extends ServiceImpl<MarketReportMapper, MarketReport> implements ReportService {

    @Override
    public MarketReport submitReport(String targetType, Long targetId, String reason, String description) {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        MarketReport report = new MarketReport();
        report.setReporterId(userId);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        report.setDescription(description);
        report.setStatus("PENDING");
        report.setCreatedAt(LocalDateTime.now());
        save(report);
        return report;
    }

    @Override
    public List<MarketReport> listMyReports() {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        return lambdaQuery()
                .eq(MarketReport::getReporterId, userId)
                .orderByDesc(MarketReport::getCreatedAt)
                .list();
    }

    @Override
    public List<MarketReport> listAllReports() {
        return lambdaQuery()
                .orderByDesc(MarketReport::getCreatedAt)
                .list();
    }

    @Override
    public MarketReport handleReport(Long id, String status, String handleResult) {
        MarketReport report = getById(id);
        if (report == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "report not found");
        }
        if (!"PENDING".equals(report.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "report already handled");
        }
        Long adminId = SecurityContextUtil.currentUser().getUserId();
        report.setStatus(status);
        report.setHandleResult(handleResult);
        report.setHandlerId(adminId);
        report.setHandledAt(LocalDateTime.now());
        updateById(report);
        return report;
    }
}
