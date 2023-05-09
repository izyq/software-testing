package com.itheima.reggie.service;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;

import java.util.Date;

public interface OrdersService {
    R submit(Orders orders);

    R page(Integer page, Integer pageSize, String number, Date beginTime, Date endTime);

    R userPage(Integer page, Integer pageSize);

    R modifyStatus(Orders orders);

    void updateStatus(String out_trade_no, String transaction_id);

    R findStatusById(String orderId);
}
