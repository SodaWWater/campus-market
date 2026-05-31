package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.OrderLog;
import com.liminghan.market.entity.PaymentRecord;
import com.liminghan.market.mapper.MarketGoodsMapper;
import com.liminghan.market.mapper.OrderLogMapper;
import com.liminghan.market.mapper.PaymentRecordMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.OrderService;
import com.liminghan.market.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements PaymentService {

    private final OrderService orderService;
    private final MarketGoodsMapper goodsMapper;
    private final OrderLogMapper orderLogMapper;

    public PaymentServiceImpl(OrderService orderService,
                              MarketGoodsMapper goodsMapper,
                              OrderLogMapper orderLogMapper) {
        this.orderService = orderService;
        this.goodsMapper = goodsMapper;
        this.orderLogMapper = orderLogMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord mockPay(Long orderId) {
        MarketOrder order = orderService.getOrder(orderId);
        Long userId = SecurityContextUtil.currentUser().getUserId();
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only buyer can pay order");
        }
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only CREATED order can be paid");
        }
        PaymentRecord existing = getByOrderId(orderId);
        if (existing != null && "SUCCESS".equals(existing.getPayStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "order has been paid");
        }

        PaymentRecord payment = existing == null ? new PaymentRecord() : existing;
        payment.setPayNo(payment.getPayNo() == null ? buildPayNo() : payment.getPayNo());
        payment.setOrderId(order.getId());
        payment.setAmount(order.getAmount());
        payment.setPayType("MOCK");
        payment.setPayStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());
        payment.setCreatedAt(payment.getCreatedAt() == null ? LocalDateTime.now() : payment.getCreatedAt());
        if (payment.getId() == null) {
            save(payment);
        } else {
            updateById(payment);
        }

        String fromStatus = order.getStatus();
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        orderService.updateById(order);
        goodsMapper.updateStatusIfMatch(order.getGoodsId(), "LOCKED", "SOLD");
        saveOrderLog(order.getId(), userId, fromStatus);
        return payment;
    }

    @Override
    public PaymentRecord getByOrderId(Long orderId) {
        return getOne(new LambdaQueryWrapper<PaymentRecord>()
                .eq(PaymentRecord::getOrderId, orderId)
                .last("LIMIT 1"), false);
    }

    private String buildPayNo() {
        return "PAY" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private void saveOrderLog(Long orderId, Long operatorId, String fromStatus) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setAction("PAY");
        log.setFromStatus(fromStatus);
        log.setToStatus("PAID");
        log.setRemark("mock payment success");
        log.setCreatedAt(LocalDateTime.now());
        orderLogMapper.insert(log);
    }
}
