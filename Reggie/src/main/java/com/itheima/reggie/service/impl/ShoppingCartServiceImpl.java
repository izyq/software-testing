package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public R findAll() {
        LambdaQueryWrapper<ShoppingCart> wapper = new LambdaQueryWrapper<>();
        wapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        wapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(wapper);
        return R.success(shoppingCarts);
    }

    @Override
    public R add(ShoppingCart shoppingCart) {

        shoppingCart.setUserId(BaseContext.getCurrentId());
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());


        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            queryWrapper.eq(null !=shoppingCart.getDishFlavor(),ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(queryWrapper);
        if (shoppingCart1 == null){
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }else {
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateById(shoppingCart1);
            shoppingCart = shoppingCart1;
        }


        if (dishId != null && shoppingCart.getDishFlavor() != null){
            return R.success("添加成功");
        }else {
            return R.success(shoppingCart);
        }


    }

    @Override
    public R clean() {
        LambdaUpdateWrapper<ShoppingCart> wapper = new LambdaUpdateWrapper<>();
        wapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartMapper.delete(wapper);
        return R.success("清除成功");
    }

    @Override
    public R sub(ShoppingCart shoppingCart) {

        LambdaUpdateWrapper<ShoppingCart> wapper = new LambdaUpdateWrapper<>();
        if (shoppingCart.getDishId() == null){
            wapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

        }else {
            wapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            wapper.eq(null !=shoppingCart.getDishFlavor(),ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }

        wapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(wapper);
        if (shoppingCart1.getNumber()<=1){
                shoppingCartMapper.deleteById(shoppingCart1.getId());
                shoppingCart1.setNumber(null);
        }else {
            shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
            shoppingCartMapper.updateById(shoppingCart1);
        }

        if (shoppingCart.getDishId() != null && shoppingCart.getDishFlavor() != null){
            return R.success("修改成功");
        }else {
            return R.success(shoppingCart1);
        }
    }
}
