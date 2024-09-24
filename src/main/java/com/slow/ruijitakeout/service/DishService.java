package com.slow.ruijitakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slow.ruijitakeout.domain.Dish;
import com.slow.ruijitakeout.domain.DishFlavor;
import com.slow.ruijitakeout.dto.DishDto;

import java.util.ArrayList;
import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * @author slow
     * 新增菜品和对应的口味数据
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品和口味信息(为了修改菜品时候的回显)
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * @author slow
     * 新增菜品和对应的口味数据
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * @author slow
     * 菜品启售停售
     */
    void startOrStop(int status, List<Long> ids);

    /**
     * @author slow
     * 通过菜品id获取菜品口味数据
     */
    List<DishFlavor> getFlavorById(Long id);

}
