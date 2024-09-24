package com.slow.ruijitakeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.slow.ruijitakeout.common.R;
import com.slow.ruijitakeout.domain.Employee;
import com.slow.ruijitakeout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

import static com.slow.ruijitakeout.constant.EmployeeConstant.EMPLOYEE_LOGIN_STATE;
import static com.slow.ruijitakeout.constant.PasswordSaltConstant.SALT;

@Slf4j
@RestController
@RequestMapping("/employee")
/**
 * 员工相关接口
 * @author slow
 */
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;
    /**
     * 员工登录接口
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("员工登录接口");
        String password = employee.getPassword();
        String username = employee.getUsername();
        Employee emp = employeeService.login(username, password, request);
        if (emp==null ){
            return R.error("用户名或密码错误");
        }
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }
        return R.success(emp);
    }

    /**
     * 员工注销接口
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        log.info("用户注销接口");
        employeeService.logout(request);
        return R.success("注销成功");
    }
    /**
     * 新增员工接口(保存员工id)
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request){
        log.info("新增员工信息");
//        Employee emp = (Employee) request.getSession().getAttribute(EMPLOYEE_LOGIN_STATE);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(emp.getId());
//        employee.setCreateUser(emp.getId());
//        密码加密
        employee.setPassword(DigestUtils.md5DigestAsHex((SALT + 123456).getBytes()));
        employeeService.save(employee);
        return R.success("新增员工成功！");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name){
        log.info("分页查询 page:{},pageSize:{},name:{}",page,pageSize,name);
//        分页构造器
        Page<Employee> pageInfo = Page.of(page, pageSize);
//        条件构造器
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.hasLength(name),"name",name);
        queryWrapper.orderByDesc("update_time");
//        进行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    /**
     * 更新员工账号
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request){
        log.info("更新员工信息");
//        获取修改人信息
//        Employee emp = (Employee) request.getSession().getAttribute(EMPLOYEE_LOGIN_STATE);
//        employee.setUpdateTime(LocalDateTime.now());
//       employee.setUpdateUser(emp.getId());
       employeeService.updateById(employee);
        return R.success("操作成功");
    }
    /**
     * 获取员工信息 查询操作需要返回实体类
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id（姓名）查询员工信息");
        Employee emp = employeeService.getById(id);
        if (emp!=null){
            return R.success(emp);
        }else{
            return R.error("查无此人");
        }
    }
}
