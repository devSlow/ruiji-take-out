package com.slow.ruijitakeout.dto;

import com.slow.ruijitakeout.domain.Order;
import com.slow.ruijitakeout.domain.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Order {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;

}