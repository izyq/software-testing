package com.itheima.reggie.service;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService {
    R findSetmeal(Integer page, Integer pageSize, String name);

    R saveSetmealWithDish(SetmealDto setmealDto);

    R findSetmealById(Long id);

    R modifyByIdWithDish(SetmealDto setmealDto);

    R removeByIdsWithDish(Long[] ids);

    R findSetmealByCategroyId(Setmeal setmeal);

    R modifyByStatus(Integer status, Long[] ids);
}
