package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.entity.AdminOperationLog;
import com.liminghan.market.entity.MarketCategory;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.mapper.AdminOperationLogMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.AdminService;
import com.liminghan.market.service.CategoryService;
import com.liminghan.market.service.GoodsService;
import com.liminghan.market.service.OrderService;
import com.liminghan.market.service.SysUserService;
import com.liminghan.market.vo.AdminDashboardVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final SysUserService sysUserService;
    private final GoodsService goodsService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AdminOperationLogMapper operationLogMapper;

    public AdminServiceImpl(SysUserService sysUserService,
                            GoodsService goodsService,
                            CategoryService categoryService,
                            OrderService orderService,
                            RedisTemplate<String, Object> redisTemplate,
                            AdminOperationLogMapper operationLogMapper) {
        this.sysUserService = sysUserService;
        this.goodsService = goodsService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public AdminDashboardVO dashboard() {
        AdminDashboardVO dashboard = new AdminDashboardVO();
        dashboard.setUserCount(sysUserService.count());
        dashboard.setGoodsCount(goodsService.count());
        dashboard.setPendingGoodsCount(goodsService.count(new LambdaQueryWrapper<MarketGoods>().eq(MarketGoods::getStatus, "PENDING_AUDIT")));
        dashboard.setOnSaleGoodsCount(goodsService.count(new LambdaQueryWrapper<MarketGoods>().eq(MarketGoods::getStatus, "ON_SALE")));
        dashboard.setOrderCount(orderService.count());
        dashboard.setCreatedOrderCount(orderService.count(new LambdaQueryWrapper<MarketOrder>().eq(MarketOrder::getStatus, "CREATED")));
        dashboard.setPaidOrderCount(orderService.count(new LambdaQueryWrapper<MarketOrder>().eq(MarketOrder::getStatus, "PAID")));
        return dashboard;
    }

    @Override
    public List<SysUser> listUsers() {
        List<SysUser> users = sysUserService.lambdaQuery().orderByDesc(SysUser::getCreatedAt).list();
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public SysUser disableUser(Long id) {
        return updateUserStatus(id, "DISABLED");
    }

    @Override
    public SysUser enableUser(Long id) {
        return updateUserStatus(id, "ENABLE");
    }

    @Override
    public List<MarketGoods> listGoods() {
        return goodsService.lambdaQuery().orderByDesc(MarketGoods::getCreatedAt).list();
    }

    @Override
    public MarketCategory enableCategory(Long id) {
        MarketCategory category = categoryService.enableCategory(id);
        saveOperationLog("CATEGORY", "ENABLE", id, "enable category");
        return category;
    }

    @Override
    public MarketCategory disableCategory(Long id) {
        MarketCategory category = categoryService.disableCategory(id);
        saveOperationLog("CATEGORY", "DISABLE", id, "disable category");
        return category;
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
        saveOperationLog("GOODS", "OFF_SHELF", goods.getId(), "admin off shelf goods");
        try {
            redisTemplate.delete("market:goods:hot");
        } catch (Exception ignored) {
            // Redis unavailable: no cache to evict.
        }
        return goods;
    }

    @Override
    public MarketGoods approveGoods(Long id) {
        MarketGoods goods = goodsService.approveGoods(id);
        saveOperationLog("GOODS", "APPROVE", goods.getId(), "approve goods");
        return goods;
    }

    @Override
    public MarketGoods rejectGoods(Long id, String reason) {
        MarketGoods goods = goodsService.rejectGoods(id, reason);
        saveOperationLog("GOODS", "REJECT", goods.getId(), reason);
        return goods;
    }

    @Override
    public List<MarketOrder> listOrders() {
        return orderService.lambdaQuery().orderByDesc(MarketOrder::getCreatedAt).list();
    }

    @Override
    public List<AdminOperationLog> listOperationLogs() {
        return operationLogMapper.selectList(new LambdaQueryWrapper<AdminOperationLog>()
                .orderByDesc(AdminOperationLog::getCreatedAt)
                .last("LIMIT 200"));
    }

    private SysUser updateUserStatus(Long id, String status) {
        Long currentAdminId = SecurityContextUtil.currentUser().getUserId();
        if (currentAdminId.equals(id)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "cannot change current admin status");
        }
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "user not found");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserService.updateById(user);
        saveOperationLog("USER", status, id, "update user status to " + status);
        user.setPassword(null);
        return user;
    }

    private void saveOperationLog(String module, String operation, Long targetId, String content) {
        AdminOperationLog log = new AdminOperationLog();
        log.setAdminId(SecurityContextUtil.currentUser().getUserId());
        log.setModule(module);
        log.setOperation(operation);
        log.setTargetId(targetId);
        log.setContent(content);
        log.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(log);
    }
}
