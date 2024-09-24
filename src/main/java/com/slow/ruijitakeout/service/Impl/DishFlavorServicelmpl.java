package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.domain.DishFlavor;
import com.slow.ruijitakeout.mapper.DishFlavorMapper;
import com.slow.ruijitakeout.service.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DishFlavorServicelmpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
