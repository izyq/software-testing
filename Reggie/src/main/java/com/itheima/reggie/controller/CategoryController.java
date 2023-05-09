package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R addCategory(@RequestBody Category category){
        R r = categoryService.addCategory(category);
        return r;
    }
    
    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R page(Integer page,Integer pageSize){
        R r = categoryService.page(page,pageSize);
        return r;
    }
    
    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping()
    public R removeCategoryById(Long id){
        R r = categoryService.removeCategoryById(id);
        return r;
    }
    
    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R modifyCategoryById(@RequestBody Category category){
        R r = categoryService.modifyCategoryById(category);
        return r;
    }
    

    @GetMapping("/list")
    public R findListByType(Integer type){
        R r = categoryService.findListByType(type);
        return r;
    }

}
