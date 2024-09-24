package com.slow.ruijitakeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.Category;
import com.slow.ruijitakeout.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类相关接口的Controller
 */
@RestController
@Slf4j
@RequestMapping("/category")
/**
 * 分类管理相关接口
 */ public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 新增分类 type=1 菜品分类，type=2 套餐分类
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        log.info("新增分类：{}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分类分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("分页查询 page:{},pageSize:{}", page, pageSize);
        Page<Category> pageInfo = Page.of(page, pageSize);
//        编写查询条件构造器
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("update_time");
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> deleteCategories(@RequestParam Long ids) {
//        通过id进行查询
        log.info("删除分类，id为：{}", ids);
//        需要判断该分类是否关联了菜品，如果有关联，则不能删除
        categoryService.removeById(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }
    /**
     * 根据类型进行分类列表查询
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
