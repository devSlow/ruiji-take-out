package com.slow.ruijitakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slow.ruijitakeout.domain.User;
import com.slow.ruijitakeout.dto.UserDto;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {
    /**
     * 用户登录
     */
    User login(UserDto userDto, HttpServletRequest request);

    /**
     * 用户登出
     * @param request
     */

    void logout(HttpServletRequest request);
}
