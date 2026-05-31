package com.liminghan.market.service;

import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.SysUser;

import java.util.List;

public interface AdminService {

    List<SysUser> listUsers();

    List<MarketGoods> listGoods();

    MarketGoods offShelfGoods(Long id);

    MarketGoods approveGoods(Long id);

    MarketGoods rejectGoods(Long id, String reason);

    List<MarketOrder> listOrders();
}
