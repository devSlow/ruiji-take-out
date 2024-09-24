package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.domain.Category;
import com.slow.ruijitakeout.domain.Dish;
import com.slow.ruijitakeout.domain.Setmeal;
import com.slow.ruijitakeout.exception.CustomException;
import com.slow.ruijitakeout.mapper.CategoryMapper;
import com.slow.ruijitakeout.service.CategoryService;
import com.slow.ruijitakeout.service.DishService;
import com.slow.ruijitakeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类相关接口开发
 *
 * @author Slow
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;


    @Override
    public void removeById(Long ids) {
        /**
         * 删除分类接口实现，需要判断当前分类是否有菜品或者套餐，如果有，则不能删除
         */
        // 检查是否含有菜品
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.eq("category_id", ids);
        long dishCount = dishService.count(dishQueryWrapper);

        // 检查是否含有套餐
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.eq("category_id", ids);
        long setmealCount = setmealService.count(setmealQueryWrapper);

        // 先检查所有条件，然后再决定是否删除
        if (dishCount > 0) {
            // 无法删除，抛出异常
            log.info("当前分类关联了菜品，无法删除");
            throw new CustomException("当前分类关联了菜品，无法删除");
        }
        if (setmealCount > 0) {
            // 无法删除，抛出异常
            log.info("当前分类关联了套餐，无法删除");
            throw new CustomException("当前分类关联了套餐，无法删除");
        }
        // 没有菜品和套餐，删除分类
        super.removeById(ids);
    }
}