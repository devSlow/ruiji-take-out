package com.slow.ruijitakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slow.ruijitakeout.domain.Order;

public interface OrderService extends IService<Order> {
    /**
     * 提交用户订单数据
     * @param order
     */
    void submit(Order order);
}
