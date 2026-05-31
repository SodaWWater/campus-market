package com.liminghan.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liminghan.market.dto.CategoryRequest;
import com.liminghan.market.entity.MarketCategory;

import java.util.List;

public interface CategoryService extends IService<MarketCategory> {

    List<MarketCategory> listCategories();

    MarketCategory createCategory(CategoryRequest request);

    MarketCategory updateCategory(Long id, CategoryRequest request);

    MarketCategory enableCategory(Long id);

    MarketCategory disableCategory(Long id);

    void deleteCategory(Long id);
}
