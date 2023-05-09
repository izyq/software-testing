package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.Contents;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台商品管理
 */
@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;


    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public R addCategory(Category category) {
        categoryMapper.insert(category);
        redisTemplate.delete(Contents.CATEGROYALL);
        return R.success("添加成功");
    }

    @Override
    public R page(Integer page, Integer pageSize) {
        IPage<Category> p = new Page<>(page,pageSize);
        IPage<Category> categoryIPage = categoryMapper.selectPage(p, null);

        return R.success(categoryIPage);
    }

    @Override
    public R removeCategoryById(Long id) {

        LambdaQueryWrapper<Dish> wapper = new LambdaQueryWrapper<>();
        wapper.eq(Dish::getCategoryId,id);
        Long count = dishMapper.selectCount(wapper);
        if (count > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> wapper1 = new LambdaQueryWrapper<>();
        wapper1.eq(Setmeal::getCategoryId,id);
        Long count1 = setmealMapper.selectCount(wapper1);
        if (count1>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }


        categoryMapper.deleteById(id);
        redisTemplate.delete(Contents.CATEGROYALL);
        return R.success("分类信息删除成功");
    }

    @Override
    public R modifyCategoryById(Category category) {
        categoryMapper.updateById(category);
        redisTemplate.delete(Contents.CATEGROYALL);
        return R.success("分类信息修改成功");
    }

    @Override
    public R findListByType(Integer type) {

        List<Category> categories = (List<Category>) redisTemplate.opsForValue().get(Contents.CATEGROYALL);

        if(categories == null || categories.size() == 0){

        LambdaQueryWrapper<Category> wapper =  new LambdaQueryWrapper<>();
        wapper.eq(null != type,Category::getType,type);
        categories = categoryMapper.selectList(wapper);
        redisTemplate.opsForValue().set(Contents.CATEGROYALL,categories);
        }
        return R.success(categories);
    }
}
