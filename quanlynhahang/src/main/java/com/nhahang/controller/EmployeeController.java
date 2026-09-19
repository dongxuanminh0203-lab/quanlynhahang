package com.nhahang.controller;

import com.nhahang.dao.EmployeeDAO;
import com.nhahang.service.EmployeeService;
import com.nhahang.model.Employee;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {

    private final EmployeeService employeeService =
            new EmployeeService();

    public List<Employee> loadEmployees()
            throws SQLException {

        return employeeService.findAll();
    }

    public void addEmployee(
            Employee employee
    ) throws SQLException {

        employeeService.insert(employee);
    }

    public void updateEmployee(
            Employee employee
    ) throws SQLException {

        employeeService.update(employee);
    }

    public void deleteEmployee(
            int employeeId
    ) throws SQLException {

        employeeService.delete(employeeId);
    }
}