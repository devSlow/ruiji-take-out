package com.slow.ruijitakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.Order;
import com.slow.ruijitakeout.domain.OrderDetail;
import com.slow.ruijitakeout.domain.ShoppingCart;
import com.slow.ruijitakeout.dto.OrderDto;
import com.slow.ruijitakeout.service.OrderDetailService;
import com.slow.ruijitakeout.service.OrderService;
import com.slow.ruijitakeout.service.ShoppingCartService;
import com.slow.ruijitakeout.service.UserService;
import com.slow.ruijitakeout.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

@Autowired
private OrderService orderService;
@Autowired
private UserService userService;
@Autowired
private OrderDetailService orderDetailService;
@Autowired
private ShoppingCartService shoppingCartService;
    /**
     * 提交用户订单数据
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Order order){
        log.info("提交订单数据：{}",order);
        orderService.submit(order);
        return R.success("下单成功");
    }
    /**
     * 分页查询订单数据
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String number,String beginTime,String endTime){
        log.info("当前页数：{},总页数：{},开始时间：{},结束时间：{}",page,pageSize,beginTime,endTime);
        Page<Order> pageInfo = Page.of(page, pageSize);
//        使用条件构造器查询
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.like(number != null,"number",number);
        orderQueryWrapper.between(beginTime!=null &&endTime!=null,"order_time",beginTime,endTime);
        orderService.page(pageInfo,orderQueryWrapper);
        return R.success(pageInfo);
    }
    /**
     * 修改订单状态
     */
    @PutMapping
    public R<String> update(@RequestBody Order order){
        log.info("修改订单状态：{}",order);
        orderService.updateById(order);
        return R.success("修改成功");
    }
    /**
     * 当前用户查询订单
     */
    @Transactional
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        // 获取当前用户ID
        Long userId = BaseContext.getCurrentId();
        Page<Order> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> ordersDtoPage = new Page<>(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, Order::getUserId, userId);
        queryWrapper.orderByDesc(Order::getOrderTime);

        // 查询当前用户的订单数据
        orderService.page(pageInfo, queryWrapper);

        List<OrderDto> list = new ArrayList<>();

        // 遍历订单记录，手动转换为 OrderDto
        for (Order item : pageInfo.getRecords()) {
            OrderDto ordersDto = new OrderDto();

            // 获取订单ID并查询订单详情
            Long orderId = item.getId();
            log.info("订单id：{}",orderId);
            LambdaQueryWrapper<OrderDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> details = orderDetailService.list(wrapper);

            // 复制属性
            BeanUtils.copyProperties(item, ordersDto);
            // 设置订单详情
            ordersDto.setOrderDetails(details != null ? details : new ArrayList<>());

            // 添加到列表
            list.add(ordersDto);
        }

        // 复制分页信息
        BeanUtils.copyProperties(pageInfo, ordersDtoPage, "records");
        ordersDtoPage.setRecords(list);

        // 日志输出
        log.info("list:{}", list);

        return R.success(ordersDtoPage);
    }



    /**
     * 再来一单
     */
    /**
     * 再来一单
     * @param map
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Map<String,String> map){
        //获取order_id
        Long orderId = Long.valueOf(map.get("id"));
        //条件构造器
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        //查询订单的口味细节数据
        queryWrapper.eq(OrderDetail::getOrderId,orderId);
        List<OrderDetail> details = orderDetailService.list(queryWrapper);
        //获取用户id，待会需要set操作
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = details.stream().map((item) ->{
            ShoppingCart shoppingCart = new ShoppingCart();
            //Copy对应属性值
            BeanUtils.copyProperties(item,shoppingCart);
            //设置一下userId
            shoppingCart.setUserId(userId);
            //设置一下创建时间为当前时间
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        //加入购物车
        shoppingCartService.saveBatch(shoppingCarts);
        return R.success("喜欢吃就再来一单吖~");
    }
}
