package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.domain.Dish;
import com.slow.ruijitakeout.domain.DishFlavor;
import com.slow.ruijitakeout.dto.DishDto;
import com.slow.ruijitakeout.mapper.DishMapper;
import com.slow.ruijitakeout.service.DishFlavorService;
import com.slow.ruijitakeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@Slf4j
public class DishServicelmpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    /**
     * @author slow
     * 新增菜品和对应的口味数据
     */
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;

//   加入事务控制
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
//        基本菜品添加
        this.save(dishDto);
        log.info("添加菜品：{}",dishDto);
//        添加菜品之前 需要加入dishId
        Long dishId = dishDto.getId();
        for (DishFlavor flavor : dishDto.getFlavors()) {
            flavor.setDishId(dishId);
        }
//        加入菜品口味数据
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 根据菜品id获取菜品和口味数据
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
//        获取菜品基本数据
        Dish dish = this.getById(id);
        log.info("获取菜品基本数据：{}",dish);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
//        通过dishId获取菜品的口味数据
        QueryWrapper<DishFlavor> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("dish_id",id);
        List<DishFlavor> flavorList = dishFlavorService.list(dishQueryWrapper);
        dishDto.setFlavors(flavorList);
        return dishDto;
    }

    /**
     * @author slow
     * 更新菜品和对应的口味数据
     */
    public void updateWithFlavor(DishDto dishDto) {
//        修改基本菜品信息
        this.updateById(dishDto);
//        修改菜品口味信息
//        根据id删除菜品口味信息
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        新增菜品口味信息
        //        获取dishId
        Long dishId = dishDto.getId();
//        将dishId保存到口味集合中
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
//    保存口味数据
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 批量修改菜品状态
     * @param status
     * @param ids
     */
    @Override
    public void startOrStop(int status, List<Long> ids) {
        for (Long id : ids) {
            Dish dish = this.getById(id);
            dish.setStatus(status);
            this.updateById(dish);
        }
    }

    /**
     * 根据菜品id获取菜品口味数据
     * @param id
     * @return
     */
    @Override
    public List<DishFlavor> getFlavorById(Long id) {
        QueryWrapper<DishFlavor> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dish_id",id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        if (list != null) {
            return list;
        }
        return null;
    }
}
