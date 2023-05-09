package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    R login(Employee employee);

    R updateStatus(Employee employee);

    R findById(Long id);
}
