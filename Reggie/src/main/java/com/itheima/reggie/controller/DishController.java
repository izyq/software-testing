package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 菜品管理
 * 前台菜单列表
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R save(@RequestBody DishDto dishDto){
        R r = dishService.saveWithDishFlavor(dishDto);
        return r;
    }
    
    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R page(Integer page,Integer pageSize,String name){
        R r = dishService.findDish(page,pageSize,name);
        return r;
    }
    
    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getByIdWithFlavor(@PathVariable Long id){
        R r = dishService.getByIdWithFlavor(id);
        return r;
    }
    
    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R modifyByIdWithFlavor(@RequestBody DishDto dishDto){

        R r = dishService.modifyByIdWithFlavor(dishDto);
        return r;
    }
    
    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R removeByIdsWithFlavor(Long[] ids){
        R r = dishService.removeByIdsWithFlavor(ids);
        return r;
    }
    
    /**
     * 修改菜品
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R modifyByStatus(@PathVariable Integer status, Long[] ids){
        R r = dishService.modifyByStatus(status,ids);
        return r;
    }
    
    
    /**
     * 从数据库或redis中查询菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    private R findDishByCategroyId(Dish dish){
        R r = dishService.findDishByCategroyId(dish);
        return r;
    }

}
