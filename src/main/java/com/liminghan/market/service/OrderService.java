package com.liminghan.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liminghan.market.dto.OrderCreateRequest;
import com.liminghan.market.entity.MarketOrder;

import java.util.List;

public interface OrderService extends IService<MarketOrder> {

    MarketOrder createOrder(OrderCreateRequest request);

    List<MarketOrder> listMyBuyOrders();

    List<MarketOrder> listMySellOrders();

    MarketOrder getOrder(Long id);

    MarketOrder cancelOrder(Long id);

    MarketOrder finishOrder(Long id);
}
