package com.slow.ruijitakeout.controller;


import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.User;
import com.slow.ruijitakeout.dto.UserDto;
import com.slow.ruijitakeout.service.UserService;
import com.slow.ruijitakeout.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import static com.slow.ruijitakeout.constant.EmployeeConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        String phone = user.getPhone();
        if (!phone.isEmpty()){
//            生成验证码
            String code = MailUtils.achieveCode();
            try {
                MailUtils.sendTestMail(phone, code);
                log.info("发送邮箱验证码为：{}", code);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
//            将验证码保存到session
            request.getSession().setAttribute(phone,code);
            return R.success("验证码发送成功");
        }
     return R.error("验证码发送失败");
    }

    /**
     * 用户登陆 返回用户对象
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto userDto, HttpServletRequest request) {
        User user = userService.login(userDto, request);
        request.getSession().setAttribute("USER_LOGIN_STATE",user);
        log.info("用户登录成功，用户信息为：{}",request.getSession().getAttribute("USER_LOGIN_STATE"));
        return R.success(user);
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        log.info("员工注销接口");
        userService.logout(request);
        return R.success("注销成功");
    }
}
