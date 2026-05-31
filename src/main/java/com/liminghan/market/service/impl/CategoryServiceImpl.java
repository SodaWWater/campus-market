package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.dto.CategoryRequest;
import com.liminghan.market.entity.MarketCategory;
import com.liminghan.market.mapper.MarketCategoryMapper;
import com.liminghan.market.service.CategoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<MarketCategoryMapper, MarketCategory> implements CategoryService {

    private static final String CATEGORY_CACHE_KEY = "market:category:list";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.category-list-ttl-minutes:60}")
    private long categoryListTtlMinutes;

    public CategoryServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MarketCategory> listCategories() {
        try {
            Object cached = redisTemplate.opsForValue().get(CATEGORY_CACHE_KEY);
            if (cached instanceof List<?> list) {
                return (List<MarketCategory>) list;
            }
        } catch (Exception ignored) {
            // Redis unavailable: fall back to database.
        }

        List<MarketCategory> categories = lambdaQuery()
                .eq(MarketCategory::getStatus, "ENABLE")
                .orderByAsc(MarketCategory::getSort)
                .list();
        try {
            redisTemplate.opsForValue().set(CATEGORY_CACHE_KEY, categories, Duration.ofMinutes(categoryListTtlMinutes));
        } catch (Exception ignored) {
            // Cache failure should not break category query.
        }
        return categories;
    }

    @Override
    public MarketCategory createCategory(CategoryRequest request) {
        MarketCategory category = new MarketCategory();
        category.setName(request.getName());
        category.setSort(request.getSort() == null ? 0 : request.getSort());
        category.setStatus(request.getStatus() == null ? "ENABLE" : request.getStatus().toUpperCase());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        save(category);
        evictCache();
        return category;
    }

    @Override
    public MarketCategory updateCategory(Long id, CategoryRequest request) {
        MarketCategory category = getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "category not found");
        }
        category.setName(request.getName());
        category.setSort(request.getSort() == null ? category.getSort() : request.getSort());
        category.setStatus(request.getStatus() == null ? category.getStatus() : request.getStatus().toUpperCase());
        category.setUpdatedAt(LocalDateTime.now());
        updateById(category);
        evictCache();
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        removeById(id);
        evictCache();
    }

    private void evictCache() {
        try {
            redisTemplate.delete(CATEGORY_CACHE_KEY);
        } catch (Exception ignored) {
            // Redis unavailable: no action needed.
        }
    }
}
