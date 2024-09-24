package com.slow.ruijitakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.Setmeal;
import com.slow.ruijitakeout.domain.ShoppingCart;
import com.slow.ruijitakeout.service.SetmealService;
import com.slow.ruijitakeout.service.ShoppingCartService;
import com.slow.ruijitakeout.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
/**
 * @author slow
 * 购物车相关接口
 */
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 添加购物车 返回购物车对象，方便页面展示
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车对象：{}",shoppingCart);
//        获取用户id，区分是谁的购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
//        查询购物车中是否存在菜品或者套餐
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",currentId);
//        判断添加的是菜品还是套餐
        if (shoppingCart.getDishId()!=null){
//            添加的是菜品
            shoppingCartQueryWrapper.eq("dish_id",shoppingCart.getDishId());
        }else{
            //            添加的是菜品
            shoppingCartQueryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartQueryWrapper);

//        判断购物车中是否有数据 如果存在，累加数量
        if (cartServiceOne!=null){
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
//            return R.success(cartServiceOne);
        }else{
//        不存在 进行保存
            cartServiceOne = new ShoppingCart();
            cartServiceOne.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
        }
        return R.success(cartServiceOne);
    }
    /**
     * 展示购物车数据
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("展示购物车数据");
//        获取当前用户id进行查询
        Long currentId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",currentId);
        shoppingCartQueryWrapper.orderByDesc("create_time");
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        log.info("清空购物车");
//        根据id删除
        Long currentId = BaseContext.getCurrentId();
        QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
        shoppingCartQueryWrapper.eq("user_id",currentId);
        shoppingCartService.remove(shoppingCartQueryWrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 减少购物车套餐或菜品的数量
     * @param shoppingCart
     * @return
     */
@PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
    log.info("减少购物车套餐或菜品的数量:{}",shoppingCart);
//    得到用户id，获取菜品id或者套餐id
    Long userId = BaseContext.getCurrentId();
    shoppingCart.setUserId(userId);
    QueryWrapper<ShoppingCart> shoppingCartQueryWrapper = new QueryWrapper<>();
//    获取当前用户的购物车
    shoppingCartQueryWrapper.eq("user_id",userId);
    if (shoppingCart.getDishId()!=null){
//        减少的是菜品
        shoppingCartQueryWrapper.eq("dish_id",shoppingCart.getDishId());
    }else{
//        减少的是套餐
        shoppingCartQueryWrapper.eq("setmeal_id",shoppingCart.getSetmealId());
    }
//    获取购物车对象
    ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartQueryWrapper);
//    数量先进行减1，能更新就更新，不能更新就删除
    cartServiceOne.setNumber(cartServiceOne.getNumber()-1);
    Integer currentNumber = cartServiceOne.getNumber();
    if (currentNumber>0){
        shoppingCartService.updateById(cartServiceOne);
    } else if (currentNumber == 0) {
        shoppingCartService.removeById(cartServiceOne);
    }
    return R.success(cartServiceOne);
    }
}