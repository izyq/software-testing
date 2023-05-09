package com.itheima.reggie.service;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService {
    R saveWithDishFlavor(DishDto dishDto);

    R findDish(Integer page, Integer pageSize, String name);

    R getByIdWithFlavor(Long id);

    R modifyByIdWithFlavor(DishDto dishDto);

    R removeByIdsWithFlavor(Long[] ids);


    R findDishByCategroyId(Dish dish);

    R modifyByStatus(Integer status, Long[] ids);
}
