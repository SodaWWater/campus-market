package com.liminghan.market.service.impl;

import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.service.AdminService;
import com.liminghan.market.service.GoodsService;
import com.liminghan.market.service.OrderService;
import com.liminghan.market.service.SysUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final SysUserService sysUserService;
    private final GoodsService goodsService;
    private final OrderService orderService;

    public AdminServiceImpl(SysUserService sysUserService, GoodsService goodsService, OrderService orderService) {
        this.sysUserService = sysUserService;
        this.goodsService = goodsService;
        this.orderService = orderService;
    }

    @Override
    public List<SysUser> listUsers() {
        return sysUserService.lambdaQuery().orderByDesc(SysUser::getCreatedAt).list();
    }

    @Override
    public List<MarketGoods> listGoods() {
        return goodsService.lambdaQuery().orderByDesc(MarketGoods::getCreatedAt).list();
    }

    @Override
    public MarketGoods offShelfGoods(Long id) {
        MarketGoods goods = goodsService.getById(id);
        if (goods == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "goods not found");
        }
        goods.setStatus("OFF_SHELF");
        goods.setUpdatedAt(LocalDateTime.now());
        goodsService.updateById(goods);
        return goods;
    }

    @Override
    public List<MarketOrder> listOrders() {
        return orderService.lambdaQuery().orderByDesc(MarketOrder::getCreatedAt).list();
    }
}
