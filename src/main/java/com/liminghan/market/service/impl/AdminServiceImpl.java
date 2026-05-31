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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final SysUserService sysUserService;
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;

    public AdminServiceImpl(SysUserService sysUserService,
                            GoodsService goodsService,
                            OrderService orderService,
                            RedisTemplate<String, Object> redisTemplate) {
        this.sysUserService = sysUserService;
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
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
        if ("SOLD".equals(goods.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "sold goods cannot be off shelf");
        }
        goods.setStatus("OFF_SHELF");
        goods.setUpdatedAt(LocalDateTime.now());
        goodsService.updateById(goods);
        try {
            redisTemplate.delete("market:goods:hot");
        } catch (Exception ignored) {
            // Redis unavailable: no cache to evict.
        }
        return goods;
    }

    @Override
    public MarketGoods approveGoods(Long id) {
        return goodsService.approveGoods(id);
    }

    @Override
    public MarketGoods rejectGoods(Long id, String reason) {
        return goodsService.rejectGoods(id, reason);
    }

    @Override
    public List<MarketOrder> listOrders() {
        return orderService.lambdaQuery().orderByDesc(MarketOrder::getCreatedAt).list();
    }
}
