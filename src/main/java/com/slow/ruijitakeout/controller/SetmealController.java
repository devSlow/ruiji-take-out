package com.slow.ruijitakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.Category;
import com.slow.ruijitakeout.domain.Dish;
import com.slow.ruijitakeout.domain.Setmeal;
import com.slow.ruijitakeout.domain.SetmealDish;
import com.slow.ruijitakeout.dto.DishDto;
import com.slow.ruijitakeout.dto.SetmealDto;
import com.slow.ruijitakeout.service.CategoryService;
import com.slow.ruijitakeout.service.DishService;
import com.slow.ruijitakeout.service.SetmealDishService;
import com.slow.ruijitakeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    /**
     * @author: Slow
     * 套餐分页查询
     */
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @RequestMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("分页查询 page:{},pageSize:{}", page, pageSize, name);
//        构造分页对象
        Page<Setmeal> pageInfo = Page.of(page, pageSize);
        Page<SetmealDto> pageInfoDto = new Page<>();
//        构造查询条件构造器
        QueryWrapper<Setmeal> setmealQueryWrapper = new QueryWrapper<>();
        setmealQueryWrapper.like(name != null, "name", name).orderByAsc("name").orderByDesc("update_time");
//        调用分页查询方法
        setmealService.page(pageInfo, setmealQueryWrapper);
//         展示分类名称


//     // 处理分页数据并拷贝属性
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        for (Setmeal setmeal : pageInfo.getRecords()) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            // 查询分类名称并设置
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            setmealDtoList.add(setmealDto);
        }

        // 将数据设置到 pageInfoDto中
        pageInfoDto.setRecords(setmealDtoList);
        BeanUtils.copyProperties(pageInfo, pageInfoDto, "records");

        return R.success(pageInfoDto);
    }

    /**
     * 新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐.....");
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 根据id删除套餐 可以删除多个
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        log.info("根据id删除套餐...");
        for (String s : ids.split(",")) {
            setmealService.removeById(s);
        }
        return R.success("删除成功");
    }

    /**
     * 根据套餐id查询套餐信息回显到页面
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        log.info("根据套餐id查询套餐信息：{}", id);
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }
    /**
    *更新套餐信息
    */
    @PutMapping
    public R<String> updateWithDish(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐信息：{}",setmealDto);
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }

    /**
     * @author slow
     * 菜品售卖状态修改
     */
    @PostMapping("/status/{status}")
    public R<String> startOrStop(@PathVariable int status, @RequestParam String ids) {
        log.info("状态是：{},id为：{}", status, ids);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        String[] split = ids.split(",");
        queryWrapper.in("id", split);
//        获取单个id
        List<Setmeal> list = setmealService.list(queryWrapper);
        for (Setmeal setmeal : list) {
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("起售停售状态修改成功");
    }
    /**
     * 获取套餐列表
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list (Setmeal setmeal){
        log.info("套餐查询：{}",setmeal);
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id",setmeal.getCategoryId()).eq("status",1);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
    /**
     * 根据套餐id查看套餐详情
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> showSetmealDish(@PathVariable Long id) {
        // 条件构造器
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);

        // 查询数据
        List<SetmealDish> records = setmealDishService.list(dishLambdaQueryWrapper);

        // 如果没有记录，返回空列表
        if (records.isEmpty()) {
            return R.success(new ArrayList<>());
        }

        List<DishDto> dtoList = new ArrayList<>();
        for (SetmealDish item : records) {
            if (item == null) {
                continue; // 跳过 null 项
            }

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long dishId = item.getDishId();
            Dish dish = dishService.getById(dishId);

            if (dish != null) {
                BeanUtils.copyProperties(dish, dishDto);
            }

            dtoList.add(dishDto);
        }

        return R.success(dtoList);
    }

}
