package com.itheima.reggie.service;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;

public interface ShoppingCartService {
    R findAll();

    R add(ShoppingCart shoppingCart);

    R clean();

    R sub(ShoppingCart shoppingCart);
}
