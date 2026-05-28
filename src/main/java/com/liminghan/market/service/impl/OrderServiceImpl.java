package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.dto.OrderCreateRequest;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.mapper.MarketOrderMapper;
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

    public OrderServiceImpl(GoodsService goodsService) {
        this.goodsService = goodsService;
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

        goods.setStatus("LOCKED");
        goods.setUpdatedAt(LocalDateTime.now());
        goodsService.updateById(goods);
        return order;
    }

    @Override
    public List<MarketOrder> listMyOrders() {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        return lambdaQuery()
                .and(query -> query.eq(MarketOrder::getBuyerId, userId).or().eq(MarketOrder::getSellerId, userId))
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
    public MarketOrder payOrder(Long id) {
        MarketOrder order = getOrder(id);
        Long userId = SecurityContextUtil.currentUser().getUserId();
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only buyer can pay order");
        }
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only CREATED order can be paid");
        }
        order.setStatus("PAID");
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);

        MarketGoods goods = goodsService.getGoods(order.getGoodsId());
        goods.setStatus("SOLD");
        goods.setUpdatedAt(LocalDateTime.now());
        goodsService.updateById(goods);
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
        order.setStatus("CANCELED");
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);

        MarketGoods goods = goodsService.getGoods(order.getGoodsId());
        goods.setStatus("ON_SALE");
        goods.setUpdatedAt(LocalDateTime.now());
        goodsService.updateById(goods);
        return order;
    }

    @Override
    public MarketOrder finishOrder(Long id) {
        MarketOrder order = getOrder(id);
        Long userId = SecurityContextUtil.currentUser().getUserId();
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only order participants can finish order");
        }
        if (!"PAID".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only PAID order can be finished");
        }
        order.setStatus("FINISHED");
        order.setUpdatedAt(LocalDateTime.now());
        updateById(order);
        return order;
    }

    private String buildOrderNo() {
        return "MO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
