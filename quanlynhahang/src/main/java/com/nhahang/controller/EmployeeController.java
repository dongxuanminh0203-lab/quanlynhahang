package com.nhahang.controller;

import com.nhahang.dao.EmployeeDAO;
import com.nhahang.service.EmployeeService;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {

    private final EmployeeService employeeService =
            new EmployeeService();

    public List<EmployeeDAO.EmployeeRecord> loadEmployees()
            throws SQLException {

        return employeeService.findAll();
    }

    public void addEmployee(
            EmployeeDAO.EmployeeRecord employee
    ) throws SQLException {

        employeeService.insert(employee);
    }

    public void updateEmployee(
            EmployeeDAO.EmployeeRecord employee
    ) throws SQLException {

        employeeService.update(employee);
    }

    public void deleteEmployee(
            int employeeId
    ) throws SQLException {

        employeeService.delete(employeeId);
    }
}