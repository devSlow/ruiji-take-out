package com.slow.ruijitakeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.slow.ruijitakeout.domain.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService  extends IService<Employee> {

/**
 * 员工登录接口
 */
Employee login(String username, String password,HttpServletRequest request);

/**
 * 员工注销接口
 */
void logout(HttpServletRequest request);

/**
 * 员工信息脱敏处理
 */
Employee getDesensitizationEmployee(Employee originEmployee);
}
