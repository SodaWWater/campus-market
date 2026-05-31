package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.dto.GoodsCreateRequest;
import com.liminghan.market.dto.GoodsUpdateRequest;
import com.liminghan.market.entity.GoodsAuditLog;
import com.liminghan.market.entity.MarketCategory;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketGoodsImage;
import com.liminghan.market.mapper.GoodsAuditLogMapper;
import com.liminghan.market.mapper.MarketGoodsImageMapper;
import com.liminghan.market.mapper.MarketGoodsMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.CategoryService;
import com.liminghan.market.service.GoodsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class GoodsServiceImpl extends ServiceImpl<MarketGoodsMapper, MarketGoods> implements GoodsService {

    private static final Set<String> EDITABLE_STATUS = Set.of("DRAFT", "PENDING_AUDIT", "REJECTED", "ON_SALE");
    private static final String HOT_GOODS_CACHE_KEY = "market:goods:hot";

    private final CategoryService categoryService;
    private final MarketGoodsImageMapper goodsImageMapper;
    private final GoodsAuditLogMapper goodsAuditLogMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.hot-goods-ttl-minutes:10}")
    private long hotGoodsTtlMinutes;

    public GoodsServiceImpl(CategoryService categoryService,
                            MarketGoodsImageMapper goodsImageMapper,
                            GoodsAuditLogMapper goodsAuditLogMapper,
                            RedisTemplate<String, Object> redisTemplate) {
        this.categoryService = categoryService;
        this.goodsImageMapper = goodsImageMapper;
        this.goodsAuditLogMapper = goodsAuditLogMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketGoods createGoods(GoodsCreateRequest request) {
        validateCategory(request.getCategoryId());
        validateImages(request.getImageUrls());

        MarketGoods goods = new MarketGoods();
        goods.setSellerId(SecurityContextUtil.currentUser().getUserId());
        goods.setCategoryId(request.getCategoryId());
        goods.setTitle(request.getTitle());
        goods.setDescription(request.getDescription());
        goods.setPrice(request.getPrice());
        goods.setConditionLevel(defaultCondition(request.getConditionLevel()));
        goods.setTradeLocation(request.getTradeLocation());
        goods.setCoverImage(firstImage(request.getImageUrls()));
        goods.setStatus("PENDING_AUDIT");
        goods.setViewCount(0);
        goods.setFavoriteCount(0);
        goods.setCreatedAt(LocalDateTime.now());
        goods.setUpdatedAt(LocalDateTime.now());
        save(goods);
        replaceImages(goods.getId(), request.getImageUrls());
        evictHotGoodsCache();
        return goods;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketGoods updateGoods(Long id, GoodsUpdateRequest request) {
        MarketGoods goods = getGoods(id);
        ensureSeller(goods);
        if (!EDITABLE_STATUS.contains(goods.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "current goods status cannot be edited");
        }
        if (request.getCategoryId() != null) {
            validateCategory(request.getCategoryId());
            goods.setCategoryId(request.getCategoryId());
        }
        if (request.getTitle() != null) {
            goods.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            goods.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "price must be greater than 0");
            }
            goods.setPrice(request.getPrice());
        }
        if (request.getConditionLevel() != null) {
            goods.setConditionLevel(defaultCondition(request.getConditionLevel()));
        }
        if (request.getTradeLocation() != null) {
            goods.setTradeLocation(request.getTradeLocation());
        }
        if (request.getImageUrls() != null) {
            validateImages(request.getImageUrls());
            goods.setCoverImage(firstImage(request.getImageUrls()));
            replaceImages(goods.getId(), request.getImageUrls());
        }
        goods.setStatus("PENDING_AUDIT");
        goods.setAuditReason(null);
        goods.setUpdatedAt(LocalDateTime.now());
        updateById(goods);
        evictHotGoodsCache();
        return goods;
    }

    @Override
    public MarketGoods offShelfMyGoods(Long id) {
        MarketGoods goods = getGoods(id);
        ensureSeller(goods);
        if ("LOCKED".equals(goods.getStatus()) || "SOLD".equals(goods.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "locked or sold goods cannot be off shelf by seller");
        }
        goods.setStatus("OFF_SHELF");
        goods.setUpdatedAt(LocalDateTime.now());
        updateById(goods);
        evictHotGoodsCache();
        return goods;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IPage<MarketGoods> pageGoods(long current,
                                        long size,
                                        String keyword,
                                        Long categoryId,
                                        String conditionLevel,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice) {
        boolean cacheableHotPage = current == 1 && size <= 10
                && isBlank(keyword)
                && categoryId == null
                && isBlank(conditionLevel)
                && minPrice == null
                && maxPrice == null;
        if (cacheableHotPage) {
            try {
                Object cached = redisTemplate.opsForValue().get(HOT_GOODS_CACHE_KEY);
                if (cached instanceof IPage<?>) {
                    return (IPage<MarketGoods>) cached;
                }
            } catch (Exception ignored) {
                // Redis unavailable: fall back to database.
            }
        }
        LambdaQueryWrapper<MarketGoods> query = new LambdaQueryWrapper<MarketGoods>()
                .eq(MarketGoods::getStatus, "ON_SALE")
                .like(!isBlank(keyword), MarketGoods::getTitle, keyword)
                .eq(categoryId != null, MarketGoods::getCategoryId, categoryId)
                .eq(!isBlank(conditionLevel), MarketGoods::getConditionLevel, conditionLevel)
                .ge(minPrice != null, MarketGoods::getPrice, minPrice)
                .le(maxPrice != null, MarketGoods::getPrice, maxPrice)
                .orderByDesc(MarketGoods::getCreatedAt);
        IPage<MarketGoods> page = page(Page.of(current, size), query);
        if (cacheableHotPage) {
            try {
                redisTemplate.opsForValue().set(HOT_GOODS_CACHE_KEY, page, Duration.ofMinutes(hotGoodsTtlMinutes));
            } catch (Exception ignored) {
                // Cache failure should not break goods query.
            }
        }
        return page;
    }

    @Override
    public IPage<MarketGoods> pageMyGoods(long current, long size, String status) {
        Long sellerId = SecurityContextUtil.currentUser().getUserId();
        return lambdaQuery()
                .eq(MarketGoods::getSellerId, sellerId)
                .eq(!isBlank(status), MarketGoods::getStatus, status)
                .orderByDesc(MarketGoods::getCreatedAt)
                .page(Page.of(current, size));
    }

    @Override
    public MarketGoods getGoods(Long id) {
        MarketGoods goods = getById(id);
        if (goods == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "goods not found");
        }
        return goods;
    }

    @Override
    public MarketGoods getPublicGoods(Long id) {
        MarketGoods goods = getGoods(id);
        if (!"ON_SALE".equals(goods.getStatus())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "goods not found");
        }
        goods.setViewCount(goods.getViewCount() == null ? 1 : goods.getViewCount() + 1);
        updateById(goods);
        return goods;
    }

    @Override
    public MarketGoods submitAudit(Long id) {
        MarketGoods goods = getGoods(id);
        ensureSeller(goods);
        if (!"DRAFT".equals(goods.getStatus()) && !"REJECTED".equals(goods.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only draft or rejected goods can be submitted");
        }
        goods.setStatus("PENDING_AUDIT");
        goods.setAuditReason(null);
        goods.setUpdatedAt(LocalDateTime.now());
        updateById(goods);
        return goods;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketGoods approveGoods(Long id) {
        MarketGoods goods = getGoods(id);
        if (!"PENDING_AUDIT".equals(goods.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only pending audit goods can be approved");
        }
        goods.setStatus("ON_SALE");
        goods.setAuditReason(null);
        goods.setUpdatedAt(LocalDateTime.now());
        updateById(goods);
        saveAuditLog(goods.getId(), "APPROVED", "approved");
        evictHotGoodsCache();
        return goods;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketGoods rejectGoods(Long id, String reason) {
        MarketGoods goods = getGoods(id);
        if (!"PENDING_AUDIT".equals(goods.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only pending audit goods can be rejected");
        }
        goods.setStatus("REJECTED");
        goods.setAuditReason(reason);
        goods.setUpdatedAt(LocalDateTime.now());
        updateById(goods);
        saveAuditLog(goods.getId(), "REJECTED", reason);
        evictHotGoodsCache();
        return goods;
    }

    @Override
    public List<GoodsAuditLog> listAuditLogs(Long goodsId) {
        return goodsAuditLogMapper.selectList(new LambdaQueryWrapper<GoodsAuditLog>()
                .eq(GoodsAuditLog::getGoodsId, goodsId)
                .orderByDesc(GoodsAuditLog::getCreatedAt));
    }

    private void validateCategory(Long categoryId) {
        MarketCategory category = categoryService.getById(categoryId);
        if (category == null || !"ENABLE".equals(category.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "category is unavailable");
        }
    }

    private void ensureSeller(MarketGoods goods) {
        Long currentUserId = SecurityContextUtil.currentUser().getUserId();
        if (!goods.getSellerId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only seller can operate goods");
        }
    }

    private void validateImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        if (imageUrls.size() > 5) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "at most 5 images are allowed");
        }
    }

    private void replaceImages(Long goodsId, List<String> imageUrls) {
        if (imageUrls == null) {
            return;
        }
        goodsImageMapper.delete(new LambdaQueryWrapper<MarketGoodsImage>().eq(MarketGoodsImage::getGoodsId, goodsId));
        for (int i = 0; i < imageUrls.size(); i++) {
            MarketGoodsImage image = new MarketGoodsImage();
            image.setGoodsId(goodsId);
            image.setImageUrl(imageUrls.get(i));
            image.setSort(i + 1);
            image.setCreatedAt(LocalDateTime.now());
            goodsImageMapper.insert(image);
        }
    }

    private void saveAuditLog(Long goodsId, String result, String reason) {
        GoodsAuditLog auditLog = new GoodsAuditLog();
        auditLog.setGoodsId(goodsId);
        auditLog.setAdminId(SecurityContextUtil.currentUser().getUserId());
        auditLog.setResult(result);
        auditLog.setReason(reason);
        auditLog.setCreatedAt(LocalDateTime.now());
        goodsAuditLogMapper.insert(auditLog);
    }

    private String defaultCondition(String conditionLevel) {
        return isBlank(conditionLevel) ? "GOOD" : conditionLevel.toUpperCase();
    }

    private String firstImage(List<String> imageUrls) {
        return imageUrls == null || imageUrls.isEmpty() ? null : imageUrls.get(0);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void evictHotGoodsCache() {
        try {
            redisTemplate.delete(HOT_GOODS_CACHE_KEY);
        } catch (Exception ignored) {
            // Redis unavailable: no cache to evict.
        }
    }
}
