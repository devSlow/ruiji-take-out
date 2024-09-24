package com.slow.ruijitakeout.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.slow.ruijitakeout.domain.Employee;
import com.slow.ruijitakeout.mapper.EmployeeMapper;
import com.slow.ruijitakeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.slow.ruijitakeout.constant.EmployeeConstant.EMPLOYEE_LOGIN_STATE;
import static com.slow.ruijitakeout.constant.PasswordSaltConstant.SALT;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    EmployeeMapper employeeMapper;
    /**
     * 员工登录接口开发
     *
     * @param username
     * @param password
     * @param request
     * @return
     */
    @Override
    public Employee login(String username, String password, HttpServletRequest request) {
//       密码进行加密
        String handlePassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", handlePassword);
        Employee emp = employeeMapper.selectOne(queryWrapper);
        if (emp == null) {
            log.info("登录失败！用户名或密码错误");
            return null;
        }
        else if (emp.getStatus()!=0){
            Employee desensitizationEmployee =  getDesensitizationEmployee(emp);
            HttpSession session = request.getSession();
            session.setAttribute("EMPLOYEE_LOGIN_STATE", emp);
            log.info("登陆成功");
            return desensitizationEmployee;
        }else {
            log.info("账号被禁用");
            return emp;
        }
    }

    /**
     * 员工注销接口
     */
    @Override
    public void logout(HttpServletRequest request) {
        request.getSession().removeAttribute("EMPLOYEE_LOGIN_STATE");
    }


    /**
     * 员工信息脱敏
     * @param originEmployee
     * @return
     */
    @Override
    public Employee getDesensitizationEmployee(Employee originEmployee) {
        if (originEmployee==null){
            return null;
        }
        Employee desensitizationEmployee = new Employee();
        desensitizationEmployee.setId(originEmployee.getId());
        desensitizationEmployee.setUsername(originEmployee.getUsername());
        desensitizationEmployee.setName(originEmployee.getName());

        desensitizationEmployee.setPhone(originEmployee.getPhone());
        desensitizationEmployee.setSex(originEmployee.getSex());
        desensitizationEmployee.setIdNumber(originEmployee.getIdNumber());
        desensitizationEmployee.setStatus(originEmployee.getStatus());
        desensitizationEmployee.setCreateTime(originEmployee.getCreateTime());
        desensitizationEmployee.setUpdateTime(originEmployee.getUpdateTime());
        desensitizationEmployee.setCreateUser(originEmployee.getCreateUser());
        desensitizationEmployee.setUpdateUser(originEmployee.getUpdateUser());
        return desensitizationEmployee;
    }
}
