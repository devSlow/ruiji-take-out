package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.domain.Dish;
import com.slow.ruijitakeout.domain.DishFlavor;
import com.slow.ruijitakeout.domain.Setmeal;
import com.slow.ruijitakeout.domain.SetmealDish;
import com.slow.ruijitakeout.dto.DishDto;
import com.slow.ruijitakeout.dto.SetmealDto;
import com.slow.ruijitakeout.mapper.SetmealMapper;
import com.slow.ruijitakeout.service.SetmealDishService;
import com.slow.ruijitakeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

@Autowired
private SetmealDishService setmealDishService;
    /**
     * @author slow
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐基本数据
        this.save(setmealDto);
//        保存套餐的菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//        保存菜品之前，先保存一个setmealId
        for (SetmealDish dish : setmealDishes){
            dish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 根据套餐id查询套餐及菜品信息
     * @param id
     * @return
     */
    public SetmealDto getByIdWithDish(Long id) {
//        套餐id获取套餐基本数据
        Setmeal setmeal = this.getById(id);
        log.info("获取菜品基本数据：{}",setmeal);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
//        通过套餐获取菜品的口味数据
        QueryWrapper<SetmealDish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("setmeal_id",id);
        List<SetmealDish> flavorList = setmealDishService.list(dishQueryWrapper);
        setmealDto.setSetmealDishes(flavorList);
        return setmealDto;
    }

    /**
     * 根据套餐id修改套餐，同时需要保存套餐和菜品的关联关系
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
//        修改套餐基本信息
        this.updateById(setmealDto);
//        修改套餐菜品信息
//        根据套餐id删除套餐菜品信息
        QueryWrapper<SetmealDish> setmealDishQueryWrapper = new QueryWrapper<>();
        setmealDishQueryWrapper.eq("setmeal_id",setmealDto.getId());
        setmealDishService.remove(setmealDishQueryWrapper);
//        新增菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish dish : setmealDishes){
            dish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }
}
