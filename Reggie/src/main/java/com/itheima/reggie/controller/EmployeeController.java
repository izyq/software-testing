package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private HttpServletRequest req;

    /*
     *
     * Description: 员工登陆
     *
     * @param:  * @param employee
     * @return: {@link com.itheima.reggie.common.R}
     */
    @PostMapping("/login")
    public R login(@RequestBody Employee employee){

      R r = employeeService.login(employee);

      if (r.getCode() == 1){
          HttpSession session = req.getSession();
          session.setAttribute("employee",((Employee)r.getData()).getId());
      }

        return r;
    }


    /*
     *
     * Description:
     * 员工登出
     * @param:  * @param
     * @return: {@link com.itheima.reggie.common.R}
     */
    @PostMapping("/logout")
    public R logout(){
        req.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    
    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R addEmpolyee(@RequestBody Employee employee){
        if (employee == null ){
            return R.error("参数非法");
        }
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("employee");
        employee.setUpdateUser(userId);
        employee.setCreateUser(userId);*/

        employeeService.save(employee);

        return R.success("添加成功");
    }
    
    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R page(Integer page,Integer pageSize,String name){

        //int i = 1/0;

        Page page1 = new Page(page,pageSize);
        LambdaQueryWrapper<Employee> wapper = new LambdaQueryWrapper<>();
        wapper.like(null != name,Employee::getName,name);
        employeeService.page(page1,wapper);

        return R.success(page1);
    }

    @PutMapping
    public R updateStatus(@RequestBody Employee employee){
        /*Long empId = (Long)req.getSession().getAttribute("employee");

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);*/
       R r = employeeService.updateStatus(employee);

       return r;
    }
    
    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @GetMapping("/{id}")
    public R findById(@PathVariable Long id){

        R r = employeeService.findById(id);
        return r;
    }



}
