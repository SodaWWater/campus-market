package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.dto.OrderCreateRequest;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.OrderLog;
import com.liminghan.market.mapper.MarketGoodsMapper;
import com.liminghan.market.mapper.MarketOrderMapper;
import com.liminghan.market.mapper.OrderLogMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.GoodsService;
import com.liminghan.market.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<MarketOrderMapper, MarketOrder> implements OrderService {

    private final GoodsService goodsService;
    private final MarketGoodsMapper goodsMapper;
    private final OrderLogMapper orderLogMapper;

    public OrderServiceImpl(GoodsService goodsService,
                            MarketGoodsMapper goodsMapper,
                            OrderLogMapper orderLogMapper) {
        this.goodsService = goodsService;
        this.goodsMapper = goodsMapper;
        this.orderLogMapper = orderLogMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketOrder createOrder(OrderCreateRequest request) {
        Long buyerId = SecurityContextUtil.currentUser().getUserId();
        MarketGoods goods = goodsService.getGoods(request.getGoodsId());
        if (!"ON_SALE".equals(goods.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "goods is not on sale");
        }
        if (goods.getSellerId().equals(buyerId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "cannot buy your own goods");
        }
        int locked = goodsMapper.updateStatusIfMatch(goods.getId(), "ON_SALE", "LOCKED");
        if (locked != 1) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "goods has been locked or sold");
        }

        MarketOrder order = new MarketOrder();
        order.setOrderNo(buildOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getSellerId());
        order.setGoodsId(goods.getId());
        order.setAmount(goods.getPrice());
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        save(order);
        saveOrderLog(order.getId(), buyerId, "CREATE", null, "CREATED", "create order and lock goods");
        return order;
    }

    @Override
    public List<MarketOrder> listMyBuyOrders() {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        return lambdaQuery()
                .eq(MarketOrder::getBuyerId, userId)
                .orderByDesc(MarketOrder::getCreatedAt)
                .list();
    }

    @Override
    public List<MarketOrder> listMySellOrders() {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        return lambdaQuery()
                .eq(MarketOrder::getSellerId, userId)
                .orderByDesc(MarketOrder::getCreatedAt)
                .list();
    }

    @Override
    public MarketOrder getOrder(Long id) {
        MarketOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "order not found");
        }
        Long userId = SecurityContextUtil.currentUser().getUserId();
        String role = SecurityContextUtil.currentUser().getRole();
        if (!"ADMIN".equals(role) && !order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "cannot access this order");
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketOrder cancelOrder(Long id) {
        MarketOrder order = getOrder(id);
        Long userId = SecurityContextUtil.currentUser().getUserId();
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only buyer can cancel order");
        }
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only CREATED order can be canceled");
        }
        String fromStatus = order.getStatus();
        order.setStatus("CANCELED");
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
        goodsMapper.updateStatusIfMatch(order.getGoodsId(), "LOCKED", "ON_SALE");
        saveOrderLog(order.getId(), userId, "CANCEL", fromStatus, "CANCELED", "cancel unpaid order and release goods");
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketOrder finishOrder(Long id) {
        MarketOrder order = getOrder(id);
        Long userId = SecurityContextUtil.currentUser().getUserId();
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only order participants can finish order");
        }
        if (!"PAID".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only PAID order can be finished");
        }
        String fromStatus = order.getStatus();
        order.setStatus("FINISHED");
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
        saveOrderLog(order.getId(), userId, "FINISH", fromStatus, "FINISHED", "finish campus offline transaction");
        return order;
    }

    private String buildOrderNo() {
        return "CM" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private void saveOrderLog(Long orderId,
                              Long operatorId,
                              String action,
                              String fromStatus,
                              String toStatus,
                              String remark) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setAction(action);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        orderLogMapper.insert(log);
    }
}
