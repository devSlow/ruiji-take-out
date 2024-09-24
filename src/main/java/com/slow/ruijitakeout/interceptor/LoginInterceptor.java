package com.slow.ruijitakeout.interceptor;

import com.alibaba.fastjson.JSON;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.Employee;
import com.slow.ruijitakeout.domain.User;
import com.slow.ruijitakeout.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.slow.ruijitakeout.constant.EmployeeConstant.EMPLOYEE_LOGIN_STATE;
import static com.slow.ruijitakeout.constant.EmployeeConstant.USER_LOGIN_STATE;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 控制器方法执行之前
        Object employeeLoginState =  request.getSession().getAttribute("EMPLOYEE_LOGIN_STATE");
        Object userLoginState = request.getSession().getAttribute("USER_LOGIN_STATE");
        log.info("当前管理员登录状态: {}", employeeLoginState);
        log.info("当前用户登录状态: {}", userLoginState);


        if (employeeLoginState == null && userLoginState == null) {
            log.info("当前未登录,开始进行拦截");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;
        }

        // 如果是员工登录
        if (employeeLoginState != null) {
            Employee employee = (Employee) employeeLoginState;
            Long employeeId = employee.getId();
            BaseContext.setCurrentId(employeeId);
            log.info("设置的员工Id: {}", employeeId);
        }

        // 如果是用户登录
        if (userLoginState != null) {
            User user = (User) userLoginState;
            Long userId = user.getId();
            BaseContext.setCurrentId(userId);
            log.info("设置的用户Id: {}", userId);
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        控制器方法执行之后
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//       视图渲染之后
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
