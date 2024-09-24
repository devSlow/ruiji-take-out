package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.domain.ShoppingCart;
import com.slow.ruijitakeout.mapper.ShoppingCartMapper;
import com.slow.ruijitakeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShoppingCartImpl extends ServiceImpl<ShoppingCartMapper,ShoppingCart> implements ShoppingCartService {
}
