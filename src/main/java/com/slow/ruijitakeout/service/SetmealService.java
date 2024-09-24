package com.slow.ruijitakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slow.ruijitakeout.domain.Setmeal;
import com.slow.ruijitakeout.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * @author slow
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 根据套餐id查询套餐及菜品信息
     */
    SetmealDto getByIdWithDish(Long id);
    /**
     * 修改套餐，同时需要保存套餐和菜品的关联关系
     */
    void updateWithDish(SetmealDto setmealDto);

}
