package com.slow.ruijitakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.*;
import com.slow.ruijitakeout.dto.DishDto;
import com.slow.ruijitakeout.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Slow
 * 菜品相关Controller
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 菜品分页查询
     */
    //    分页查询一般使用GetMapping
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("分页查询 page:{},pageSize:{}", page, pageSize, name);
//        构造分页对象
        Page<Dish> pageInfo = Page.of(page, pageSize);
        Page<DishDto> pageInfoDto = new Page<>();
//        构造查询条件构造器
        QueryWrapper<Dish> dishQueryWrapper = new QueryWrapper<>();
        dishQueryWrapper.like(name != null, "name", name).orderByAsc("name").orderByDesc("update_time");
//        调用分页查询方法
        dishService.page(pageInfo, dishQueryWrapper);
//         展示分类名称


//     // 处理分页数据并拷贝属性
        List<DishDto> dishDtoList = new ArrayList<>();
        for (Dish dish : pageInfo.getRecords()) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            // 查询分类名称并设置
            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            dishDtoList.add(dishDto);
        }

        // 将数据设置到 pageInfoDto中
        pageInfoDto.setRecords(dishDtoList);
        BeanUtils.copyProperties(pageInfo, pageInfoDto, "records");

        return R.success(pageInfoDto);
    }

    /**
     * @author slow
     * 新增带有口味数据的菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("新增菜品成功：{}", dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * @author slow
     * 根据id查询菜品和口味信息(为了修改菜品时候的回显)
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        log.info("根据id查询菜品信息：{}", id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * @author slow
     * 修改菜品
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("菜品修改成功：{}", dishDto);
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品修改成功");
    }

    /**
     * @author slow
     * 根据id删除菜品(可以批量删除)
     */
    @DeleteMapping
    public R<String> deleteById(String ids) {
        log.info("删除菜品：{}", ids);
        List<String> list = new ArrayList<>();
        for (String s : ids.split(",")) {
//            获取每一个id
            list.add(s);
        }
        dishService.removeByIds(list);
        return R.success("删除成功");
    }

    /**
     * @author slow
     * 菜品售卖状态修改
     */
    @PostMapping("/status/{status}")
    public R<String> startOrStop(@PathVariable int status, @RequestParam List<Long> ids) {
        dishService.startOrStop(status, ids);
        return R.success("起售停售状态修改成功");
    }
    /**
     * 根据分类id查询菜品列表
     */

    /**
     * 根据分类id查询菜品及口味数据列表
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId) {
        log.info("根据分类id查询菜品列表：{}", categoryId);
        QueryWrapper<Dish> dishDtoQueryWrapper = new QueryWrapper<>();
//        返回基本数据
        dishDtoQueryWrapper.eq("category_id", categoryId).eq("status", 1);
        List<Dish> dishList = dishService.list(dishDtoQueryWrapper);
        log.info("基本菜品列表：{}",dishList);
//        BeanUtils只能单个对象复制
        List<DishDto> dishDtoList = new ArrayList<>();
        DishDto dishDto = new DishDto();
        for (Dish dish : dishList){
            BeanUtils.copyProperties(dish,dishDto);
            dishDtoList.add(dishDto);
        }
        log.info("带有口味的菜品列表：{}",dishDtoList);
//        返回口味集合
        List<DishFlavor> dishFlavorList=new ArrayList<>();
        for (DishDto dish : dishDtoList) {
//            获取dishId
            Long id = dish.getId();
//          通过dishId查询每个菜品口味数据
            dishFlavorList  = dishService.getFlavorById(id);
        }
        dishDto.setFlavors(dishFlavorList);
        return R.success(dishDtoList);
    }
}
