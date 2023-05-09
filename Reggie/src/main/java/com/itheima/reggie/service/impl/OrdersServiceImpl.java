package com.itheima.reggie.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.wxpay.sdk.WXPay;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.config.WeiXinConfig;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.*;
import com.itheima.reggie.service.OrdersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public R submit(Orders orders) {
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();

        //查询用户
        User user = userMapper.selectById(currentId);

        //查询购物车内容
        LambdaQueryWrapper<ShoppingCart> wapper = new LambdaQueryWrapper<>();
        wapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(wapper);

        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，不能下单");
        }

        //获取地址
        LambdaQueryWrapper<AddressBook> wapper1 = new LambdaQueryWrapper<>();
        wapper1.eq(AddressBook::getId,orders.getAddressBookId());
        AddressBook addressBook = addressBookMapper.selectOne(wapper1);
        if(addressBook == null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //获取ordersId
        long orderId = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);

        //计算金额
        for (ShoppingCart shoppingCart : shoppingCarts) {
           amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
        }

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(1);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        ordersMapper.insert(orders);

        String s = JSON.toJSONString(shoppingCarts);
        List<OrderDetail> orderDetails = JSON.parseArray(s, OrderDetail.class);

        orderDetails.stream().forEach(o->{
            o.setOrderId(orders.getId());
            orderDetailMapper.insert(o);
        });

        /*for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setAmount(shoppingCart.getAmount());

            orderDetailMapper.insert(orderDetail);
        }*/

        try {
            //清空购物车
            shoppingCartMapper.delete(wapper);
            //1.创建一个微信支付核心对象
            WeiXinConfig weiXinConfig = new WeiXinConfig();
            //CONFIG配置类
            WXPay wxPay = new WXPay(weiXinConfig);
            //3.可以进行微信支付接口的调用了
            //统一下单接口
            //4.wxPay.unifiedOrder(); = 调用微信的支付接口  参数map
            Map<String, String> requsetMap = new HashMap<>();
            //购买的商品信息
            requsetMap.put("body", "瑞吉外卖");
            //商家的订单号
            requsetMap.put("out_trade_no",orders.getId()+"");
            //付款金额
            requsetMap.put("total_fee","1");//以分为单位

            requsetMap.put("spbill_create_ip","127.0.0.1");
            //回调通知  商家接收微信的信息的接口
            // requsetMap.put("notify_url","http://4q7t417291.zicp.vip/wx/notify");
            requsetMap.put("notify_url","null");
            //支付类型
            requsetMap.put("trade_type","NATIVE");

            //模拟浏览器发送一个http请求
            /**
             * requsetMap 代表请求参数
             * responseMap  返回值
             */
            Map<String, String> responseMap = wxPay.unifiedOrder(requsetMap);
            responseMap.put("out_trade_no",orders.getId()+"");
            return R.success(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.success("下单失败");
    }

    @Override
    public R page(Integer page, Integer pageSize, String number, Date beginTime, Date endTime) {
        LambdaQueryWrapper<Orders> wapper = new LambdaQueryWrapper<>();
        wapper.like(null != number,Orders::getNumber,number)
                .le(null != endTime,Orders::getOrderTime,endTime)
                .ge(null != beginTime,Orders::getOrderTime,beginTime);
        IPage<Orders> p = new Page<>(page,pageSize);
        IPage<Orders> ordersIPage = ordersMapper.selectPage(p, wapper);

        /*IPage<OrdersDto> ordersDtoIPage = new Page<>();

        BeanUtils.copyProperties(ordersIPage,ordersDtoIPage,"records");*/

        List<Orders> records = ordersIPage.getRecords();
        String s = JSON.toJSONString(records);
        List<OrdersDto> ordersDtos = JSON.parseArray(s, OrdersDto.class);
        for (OrdersDto ordersDto : ordersDtos) {
            LambdaQueryWrapper<OrderDetail> wapper1 = new LambdaQueryWrapper<>();
            wapper1.eq(OrderDetail::getOrderId,ordersDto.getId());
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(wapper1);
            ordersDto.setOrderDetails(orderDetails);
        }
        String s1 = JSON.toJSONString(ordersIPage);
        Page page1 = JSON.parseObject(s1, Page.class);
        page1.setRecords(ordersDtos);

        return R.success(page1);
    }

    @Override
    public R userPage(Integer page, Integer pageSize) {
        //获取userid
        Long currentId = BaseContext.getCurrentId();
        //查询条件
        LambdaQueryWrapper<Orders> wapper = new LambdaQueryWrapper<>();
        wapper.eq(Orders::getUserId,currentId);
        wapper.orderByDesc(Orders::getOrderTime);
        IPage<Orders> p = new Page<>(page,pageSize);

        //查询语句
        IPage<Orders> ordersIPage = ordersMapper.selectPage(p, wapper);
        //最终返回的数据
        IPage<OrdersDto> ordersDtoIPage = new Page<>();
        //复制数据
        BeanUtils.copyProperties(ordersIPage,ordersDtoIPage,"records");
        //遍历获取到的订单
        List<Orders> records = ordersIPage.getRecords();
        List<OrdersDto> ordersDtos = new ArrayList<>();
        for (Orders record : records) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(record,ordersDto);
            //查询订单详情
            LambdaQueryWrapper<OrderDetail> wapper1 = new LambdaQueryWrapper<>();
            wapper1.eq(OrderDetail::getOrderId,record.getId());
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(wapper1);
            ordersDto.setOrderDetails(orderDetails);
            ordersDtos.add(ordersDto);
        }
        ordersDtoIPage.setRecords(ordersDtos);
        return R.success(ordersDtoIPage);
    }

    @Override
    public R modifyStatus(Orders orders) {
        ordersMapper.updateById(orders);
        return R.success("修改成功");
    }

    @Override
    public void updateStatus(String out_trade_no, String transaction_id) {
        Orders orders = ordersMapper.selectById(out_trade_no);
        orders.setNumber(transaction_id);
        orders.setStatus(2);
        System.out.println(orders);
        ordersMapper.updateById(orders);
    }

    @Override
    public R findStatusById(String orderId) {
        Orders orders = ordersMapper.selectById(Long.parseLong(orderId));
        if (orders.getStatus() == 2){
            return R.success("订单已支付");
        }
        return R.error("订单未支付");
    }
}
