package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public R login(Employee employee) {
        if (employee == null){
            return R.error("参数非法");
        }
        String passeord = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        LambdaQueryWrapper<Employee> wapper = new LambdaQueryWrapper<>();
        wapper.eq(Employee::getUsername,employee.getUsername());
        //Employee employeeDb = employeeMapper.selectOne(wapper);
        Employee employeeDb = this.getOne(wapper);
        if (employeeDb == null){
            return R.error("用户不存在");
        }

        if (!employeeDb.getPassword().equals(passeord)){
            return R.error("密码错误");
        }

        if (employeeDb.getStatus() != 1){
            return R.error("该用户已被禁用");
        }
        return R.success(employeeDb);
    }

    @Override
    public R updateStatus(Employee employee) {

        if (employee==null){
            return R.error("非法参数");
        }

        Employee employee1 = employeeMapper.selectById(employee.getId());

        if (employee1 == null){
            return R.error("该员工不存在");
        }

        employeeMapper.updateById(employee);

        return R.success("修改成功");
    }

    @Override
    public R findById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null){
            return R.error("该员工不存在");
        }
        return R.success(employee);
    }
}
