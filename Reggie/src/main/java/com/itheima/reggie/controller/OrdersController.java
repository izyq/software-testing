package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
/**
 * 订单
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R submit(@RequestBody Orders orders){
      return ordersService.submit(orders);
    }
    
    /**
     * 订单分页
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R page(Integer page, Integer pageSize, String number, Date beginTime,Date endTime){
      return ordersService.page(page,pageSize,number,beginTime,endTime);
    }
    
    /**
     * 订单详情
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R userPage(Integer page, Integer pageSize){
        return ordersService.userPage(page,pageSize);
    }
    
    /**
     * 修改订单
     * @param orders
     * @return
     */
    @PutMapping
    public R modifyStatus(@RequestBody Orders orders){
        return ordersService.modifyStatus(orders);
    }
    
    /**
     * 根据id查找订单
     * @param orderId
     * @return
     */
    @GetMapping("/findStatusById")
    public R findStatusById(String orderId){
           return ordersService.findStatusById(orderId);
    }
}
