package com.itheima.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public R findSetmeal(Integer page, Integer pageSize, String name) {

        //要返回的查询数据
        IPage<SetmealDto> setmealDtoIPage = new Page<>();

        //查询条件
        LambdaQueryWrapper<Setmeal> wapper = new LambdaQueryWrapper<>();
        wapper.like(null != name,Setmeal::getName,name);
        IPage<Setmeal> p = new Page<>(page,pageSize);


        //查询语句
        IPage<Setmeal> setmealIPage = setmealMapper.selectPage(p, wapper);

       /* //复制集合
        BeanUtils.copyProperties(setmealIPage,setmealDtoIPage,"records");*/
        //遍历收集数据
        List<Setmeal> records = setmealIPage.getRecords();
        String s = JSON.toJSONString(records);
        List<SetmealDto> setmealDtos = JSON.parseArray(s,SetmealDto.class);
        for (SetmealDto setmealDto : setmealDtos) {
            Category category = categoryMapper.selectById(setmealDto.getCategoryId());
            if (category != null){
                setmealDto.setCategoryName(category.getName());
            }
        }
        /*for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record,setmealDto);
            //根据CategoryId查询套餐名
            Category category = categoryMapper.selectById(record.getCategoryId());
            if (category != null){
                setmealDto.setCategoryName(category.getName());
            }
            setmealDtos.add(setmealDto);
        }*/

        String s1 = JSON.toJSONString(setmealIPage);
        Page page1 = JSON.parseObject(s1, Page.class);
        page1.setRecords(setmealDtos);

        return R.success(page1);
    }

    @CacheEvict(value = "Setmeal",key = "#root.args[0].getCategoryId+'_'+#root.args[0].getStatus")
    @Transactional
    @Override
    public R saveSetmealWithDish(SetmealDto setmealDto) {
        setmealMapper.insert(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSort(0);
            setmealDish.setIsDeleted(0);
            setmealDish.setSetmealId(setmealDto.getId());
            setmealDishMapper.insert(setmealDish);
        }
        return R.success("添加成功");
    }

    @Cacheable(value = "Setmeal",key = "#id",unless = "#result.getData() == null")
    @Override
    public R findSetmealById(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> wapper = new LambdaQueryWrapper<>();
        wapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(wapper);
        setmealDto.setSetmealDishes(setmealDishes);
        Category category = categoryMapper.selectById(setmeal.getCategoryId());
        if (category != null){
            setmealDto.setCategoryName(category.getName());
        }
        return R.success(setmealDto);
    }

    @CacheEvict(value = "Setmeal",key = "#root.args[0].getCategoryId+'_'+#root.args[0].getStatus")
    @Transactional
    @Override
    public R modifyByIdWithDish(SetmealDto setmealDto) {
        setmealMapper.updateById(setmealDto);
        LambdaUpdateWrapper<SetmealDish> wapper = new LambdaUpdateWrapper<>();
        wapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishMapper.delete(wapper);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
            setmealDishMapper.insert(setmealDish);
        }

        return R.success("修改成功");
    }

    @CacheEvict(value = "Setmeal",allEntries = true)
    @Transactional
    @Override
    public R removeByIdsWithDish(Long[] ids) {
        /*for (Long id : ids) {
            LambdaUpdateWrapper<SetmealDish> wapper = new LambdaUpdateWrapper<>();
            wapper.eq(SetmealDish::getSetmealId,id);
            setmealDishMapper.delete(wapper);
        }*/
        LambdaUpdateWrapper<SetmealDish> wapper = new LambdaUpdateWrapper<>();
        wapper.in(SetmealDish::getSetmealId,ids);
        setmealDishMapper.delete(wapper);
        setmealMapper.deleteBatchIds(Arrays.asList(ids));
        return R.success("删除成功");
    }

    @CacheEvict(value = "Setmeal",key = "#root.args[0].getCategoryId+'_'+#root.args[0].getStatus")
    @Transactional
    @Override
    public R modifyByStatus(Integer status,Long[] ids) {
        Setmeal setmeal = new Setmeal();
        /*setmeal.setStatus(status);
        for (Long id : ids) {
            setmeal.setId(id);
            setmealMapper.updateById(setmeal);
        }*/

        LambdaUpdateWrapper<Setmeal> wapper = new LambdaUpdateWrapper<>();
        wapper.set(Setmeal::getStatus,status).in(Setmeal::getId,ids);
        setmealMapper.update(setmeal,wapper);
        return R.success("修改成功");
    }



    @Cacheable(value = "Setmeal",key = "#root.args[0].getCategoryId+'_'+#root.args[0].getStatus")
    @Override
    public R findSetmealByCategroyId(Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> wapper = new LambdaQueryWrapper<>();
        wapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId())
                .eq(Setmeal::getStatus,1);
        List<Setmeal> setmeals = setmealMapper.selectList(wapper);

        List<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal setmeal1 : setmeals) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal1,setmealDto);
            //根据CategoryId查询套餐名
            Category category = categoryMapper.selectById(setmeal1.getCategoryId());
            if (category != null){
                setmealDto.setCategoryName(category.getName());
            }

            LambdaQueryWrapper<SetmealDish> wapper1 = new LambdaQueryWrapper<>();
            wapper1.eq(SetmealDish::getSetmealId,setmeal1.getId());
            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(wapper1);
            setmealDto.setSetmealDishes(setmealDishes);
            setmealDtos.add(setmealDto);
        }
        return R.success(setmealDtos);
    }


}
