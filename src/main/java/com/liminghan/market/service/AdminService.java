package com.liminghan.market.service;

import com.liminghan.market.entity.AdminOperationLog;
import com.liminghan.market.entity.MarketCategory;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.vo.AdminDashboardVO;

import java.util.List;

public interface AdminService {

    AdminDashboardVO dashboard();

    List<SysUser> listUsers();

    SysUser disableUser(Long id);

    SysUser enableUser(Long id);

    List<MarketGoods> listGoods();

    MarketCategory enableCategory(Long id);

    MarketCategory disableCategory(Long id);

    MarketGoods offShelfGoods(Long id);

    MarketGoods approveGoods(Long id);

    MarketGoods rejectGoods(Long id, String reason);

    List<MarketOrder> listOrders();

    List<AdminOperationLog> listOperationLogs();
}
