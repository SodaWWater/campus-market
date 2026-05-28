package com.liminghan.market.service;

import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.SysUser;

import java.util.List;

public interface AdminService {

    List<SysUser> listUsers();

    List<MarketGoods> listGoods();

    MarketGoods offShelfGoods(Long id);

    List<MarketOrder> listOrders();
}
