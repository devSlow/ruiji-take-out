package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.domain.User;
import com.slow.ruijitakeout.dto.UserDto;
import com.slow.ruijitakeout.mapper.UserMapper;
import com.slow.ruijitakeout.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.slow.ruijitakeout.constant.EmployeeConstant.USER_LOGIN_STATE;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 用户登录
     * @param userDto
     * @param request
     * @return
     */
    @Override
    public User login(UserDto userDto, HttpServletRequest request) {
        //        获取用户输入验证码
        String code = userDto.getCode();
        String genateCode = (String) request.getSession().getAttribute(userDto.getPhone());
        log.info("用户输入的验证码为：{}，服务器生成验证码为：{}",code,genateCode);
//        将session中的code与用户输入的code进行比较 验证码不一致
        if (!code.equals(genateCode)){
            throw new RuntimeException("验证码错误");
        }
//        查询数据库中是否存在该用户
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("phone",userDto.getPhone());
        User user = this.getOne(userQueryWrapper);
//        数据库不存在该用户
        if (user==null){
            user = new User();
            user.setPhone(userDto.getPhone());
            user.setStatus(1);
            user.setAvatar("https://img.cdn.aliyun.dcloud.net.cn/guide/uniapp/app/avatar.png");
            user.setName("用户"+userDto.getPhone());

            this.save(user);
        }
        return user;
    }

    /**
     * 用户退出
     * @param request
     */
    @Override
    public void logout(HttpServletRequest request) {
        request.getSession().removeAttribute("USER_LOGIN_STATE");
    }
}
