package com.itheima.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.Contents;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 前台菜单列表
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    //@CacheEvict是用来标注在需要清除缓存元素的方法或类上的。当标记在一个类上时表示其中所有的方法的执行都会触发缓存的清除操作
    @CacheEvict(value = "Dish",allEntries = true)
    @Transactional //事务
    @Override
    public R saveWithDishFlavor(DishDto dishDto) {

        
        dishMapper.insert(dishDto);

        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
            dishFlavorMapper.insert(flavor);
        }

        redisTemplate.delete(Contents.DISH);
        return R.success("添加成功");
    }

    @Override
    public R findDish(Integer page, Integer pageSize, String name) {

        /*//dishDto分页器
         IPage<DishDto> dishDtoIPage = new Page<>();*/
         //dish分页器
        IPage<Dish> p = new Page<>(page,pageSize);
        //查询条件
        LambdaQueryWrapper<Dish> wapper = new LambdaQueryWrapper<>();
        wapper.like(null != name,Dish::getName,name);
        //查询语句
        IPage<Dish> dishIPage = dishMapper.selectPage(p, wapper);
        /*//spring集合复制
        BeanUtils.copyProperties(dishIPage,dishDtoIPage,"records");*/
        //获取查询到的dish集合
        List<Dish> records = dishIPage.getRecords();
        String jsonString = JSON.toJSONString(records);
        List<DishDto> dishDtos = JSON.parseArray(jsonString, DishDto.class);
        dishDtos.forEach(c->{
            //根据CategoryId查询菜系
            Category category = categoryMapper.selectById(c.getCategoryId());
            if (category != null){
                c.setCategoryName(category.getName());
            }
        });
        String jsonString1 = JSON.toJSONString(dishIPage);
        Page page1 = JSON.parseObject(jsonString1, Page.class);
        page1.setRecords(dishDtos);
        /*//收集要返回的数据
        List<DishDto> dishDtos = new ArrayList<>();
        //遍历收集数据
        for (Dish record : records) {
            //创建一个dishDto
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            //根据CategoryId查询菜系
            Category category = categoryMapper.selectById(record.getCategoryId());
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }

            //查询菜的口味
            LambdaQueryWrapper<DishFlavor> wapper1 = new LambdaQueryWrapper<>();
            wapper1.eq(DishFlavor::getDishId,record.getId());
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(wapper1);
            dishDto.setFlavors(dishFlavors);
            //添加数据
            dishDtos.add(dishDto);
        }

        //将数据设置到分页器中
        dishDtoIPage.setRecords(dishDtos);*/

        return R.success(page1);
    }


    @Override
    public R getByIdWithFlavor(Long id) {
        DishDto dishDto = (DishDto) redisTemplate.opsForHash().get(Contents.DISH, id);
        if (dishDto == null ){


        Dish dish = dishMapper.selectById(id);
        dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> wapper = new LambdaQueryWrapper<>();
        wapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(wapper);
        dishDto.setFlavors(dishFlavors);
        Category category = categoryMapper.selectById(dish.getCategoryId());
        if (category != null){
            dishDto.setCategoryName(category.getName());
        }
        redisTemplate.opsForHash().put(Contents.DISH,id,dishDto);
        }
        return R.success(dishDto);
    }


    @Transactional //事务
    @Override
    public R modifyByIdWithFlavor(DishDto dishDto) {
        dishMapper.updateById(dishDto);
        LambdaUpdateWrapper<DishFlavor> wapper = new LambdaUpdateWrapper<>();
        wapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorMapper.delete(wapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishDto.getId());
            dishFlavorMapper.insert(flavor);
        }

        redisTemplate.delete(Contents.DISH);
        return R.success("修改成功");
    }


    @Transactional //事务
    @Override
    public R removeByIdsWithFlavor(Long[] ids) {
        /*for (Long id : ids) {
            LambdaUpdateWrapper<DishFlavor> wapper = new LambdaUpdateWrapper<>();
            wapper.eq(DishFlavor::getDishId,id);
            dishFlavorMapper.delete(wapper);
        }*/

        LambdaUpdateWrapper<DishFlavor> wapper = new LambdaUpdateWrapper<>();
        wapper.in(DishFlavor::getDishId,ids);
        dishFlavorMapper.delete(wapper);
        dishMapper.deleteBatchIds(Arrays.asList(ids));
        redisTemplate.delete(Contents.DISH);
        return R.success("删除成功");
    }

    @CacheEvict(value = "Dish",allEntries = true)
    @Override
    public R modifyByStatus(Integer status,Long[] ids) {
        Dish dish = new Dish();
        /*dish.setStatus(status);
        for (Long id : ids) {
            dish.setId(id);
            dishMapper.updateById(dish);
        }*/
        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Dish::getStatus,status).in(Dish::getId,ids);
        dishMapper.update(dish,wrapper);
        redisTemplate.delete(Contents.DISH);
        return R.success("修改成功");
    }
    
    /**
     * //TODO 从缓存或者数据库中查找商品
     * @param dish
     * @return
     */
    @Override
    public R findDishByCategroyId(Dish dish) {

        //从缓存中查取数据
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForHash().get("dish", dish.getCategoryId());

        //如果没有就去数据库查,然后添加到缓存中
        if (dishDtos == null || dishDtos.size() == 0){

        LambdaQueryWrapper<Dish> wapper = new LambdaQueryWrapper<>();
        wapper.eq(null != dish.getCategoryId(),Dish::getCategoryId,dish.getCategoryId())
                .like(null != dish.getName(),Dish::getName,dish.getName())
                .eq(Dish::getStatus,1) ;
        List<Dish> dishes = dishMapper.selectList(wapper);
        dishDtos = new ArrayList<>();
        for (Dish dish1 : dishes) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);
            //根据CategoryId查询菜系
            Category category = categoryMapper.selectById(dish1.getCategoryId());
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }
            //查询菜的口味
            LambdaQueryWrapper<DishFlavor> wapper1 = new LambdaQueryWrapper<>();
            wapper1.eq(DishFlavor::getDishId,dish1.getId());
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(wapper1);
            dishDto.setFlavors(dishFlavors);
            //添加数据
            dishDtos.add(dishDto);

        }
        redisTemplate.opsForHash().put(Contents.DISH,Contents.CATEGROYDISH+dish.getCategoryId(),dishDtos);
        }
        return R.success(dishDtos);
    }


}
