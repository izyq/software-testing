package com.itheima.reggie.service;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;

public interface CategoryService {
    R addCategory(Category category);

    R page(Integer page, Integer pageSize);

    R removeCategoryById(Long id);

    R modifyCategoryById(Category category);

    R findListByType(Integer type);
}
