package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    
    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R page(Integer page, Integer pageSize, String name){
        R r = setmealService.findSetmeal(page,pageSize,name);
        return r;
    }
    
    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R save(@RequestBody SetmealDto setmealDto){

        R r =  setmealService.saveSetmealWithDish(setmealDto);
        return r;
    }
    
    /**
     * 查找套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R findSetmealById(@PathVariable Long id){
        R r =  setmealService.findSetmealById(id);
        return r;
    }


    @PutMapping
    public R modifyByIdWithDish(@RequestBody SetmealDto setmealDto){

        R r = setmealService.modifyByIdWithDish(setmealDto);
        return r;
    }
    
    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R removeByIdsWithDish(Long[] ids){
        R r = setmealService.removeByIdsWithDish(ids);
        return r;
    }

    @PostMapping("/status/{status}")
    public R modifyByStatus0(@PathVariable Integer status, Long[] ids){
        R r = setmealService.modifyByStatus(status,ids);
        return r;
    }
    
    /**
     * 套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R findSetmealByCategroyId(Setmeal setmeal){
        R r = setmealService.findSetmealByCategroyId(setmeal);
        return r;
    }
}
