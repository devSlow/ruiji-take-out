package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.*;
import com.slow.ruijitakeout.exception.CustomException;
import com.slow.ruijitakeout.mapper.OrderMapper;
import com.slow.ruijitakeout.service.*;
import com.slow.ruijitakeout.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 提交用户订单数据
     * @param order
     */
    @Transactional
//    public void submit(Order order) {
////        获取用户id，判断当前用户
//        Long userId = BaseContext.getCurrentId();
////        获取用户购物车数据
//        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
//        shoppingCartQueryWrapper.eq("user_id", userId);
//        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartQueryWrapper);
//
//        if (shoppingCartList==null||shoppingCartList.size()==0){
//            throw new CustomException("购物车数据为空，不能下单！");
//        }
//        log.info("当前用户购物车数据：{}",shoppingCartList);
////        向订单表中插入一条数据
////        订单插入数据之前，需获取用户信息、地址信息
//        User user = userService.getById(userId);
//        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());
////        设置订单号
//        long orderId = IdWorker.getId();
//        order.setNumber(String.valueOf(orderId));
//
////        订单总金额， 默认为0
//        AtomicInteger amount = new AtomicInteger(0);
////         订单明细集合
//        List<OrderDetail> orderDetailList=new ArrayList<>();
//
//        for (ShoppingCart shoppingCart : shoppingCartList){
////            获取每一个购物车项信息 将其转换为一条订单明细
//            OrderDetail orderDetail = new OrderDetail();
//            orderDetail.setOrderId(orderId);
//            orderDetail.setNumber(shoppingCart.getNumber());
//            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
//            orderDetail.setDishId(shoppingCart.getDishId());
//            orderDetail.setSetmealId(shoppingCart.getSetmealId());
//            orderDetail.setName(shoppingCart.getName());
//            orderDetail.setAmount(shoppingCart.getAmount());
//            orderDetail.setImage(shoppingCart.getImage());
//            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
//            orderDetailList.add(orderDetail);
//        }
//
////        设置下单时间
//        order.setOrderTime(LocalDateTime.now());
////        设置结账时间
//        order.setCheckoutTime(LocalDateTime.now());
////        设置派送状态
//        order.setStatus(2);
////       订单总金额
//        order.setAmount(new BigDecimal(amount.get()));
//
//        if (user != null){
//            order.setUserId(userId);
//            order.setUserName(user.getName());
//            order.setConsignee(addressBook.getConsignee());
//            order.setPhone(addressBook.getPhone());
//            order.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
//            this.save(order);
//        }
////        向订单明细表中插入多条数据 封装订单明细字段
//        orderDetailService.saveBatch(orderDetailList);
//
////        清空购物车
//        shoppingCartService.remove(shoppingCartQueryWrapper);
//
//    }
    public void submit(Order order) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        //条件构造器
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据当前用户id查询其购物车数据
        shoppingCartLambdaQueryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        //判断一下购物车是否为空
        if (shoppingCarts == null) {
            throw new CustomException("购物车数据为空，不能下单");
        }
        //判断一下地址是否有误
        Long addressBookId = order.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBookId == null) {
            throw new CustomException("地址信息有误，不能下单");
        }
        //获取用户信息，为了后面赋值
        User user = userService.getById(userId);
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);
        //向订单细节表设置属性
        List<OrderDetail> orderDetailList= shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());

        //向订单表设置属性
        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        order.setStatus(2);
        order.setUserId(userId);
        order.setAddressBookId(addressBookId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setAmount(new BigDecimal(amount.get()));
        order.setPhone(addressBook.getPhone());
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setAddress(
                (addressBook.getProvinceName() == null ? "":addressBook.getProvinceName())+
                        (addressBook.getCityName() == null ? "":addressBook.getCityName())+
                        (addressBook.getDistrictName() == null ? "":addressBook.getDistrictName())+
                        (addressBook.getDetail() == null ? "":addressBook.getDetail())
        );

        //根据查询到的购物车数据，对订单表插入数据（1条）
        super.save(order);
        //根据查询到的购物车数据，对订单明细表插入数据（多条）
        orderDetailService.saveBatch(orderDetailList);
        //清空购物车数据
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }


}
