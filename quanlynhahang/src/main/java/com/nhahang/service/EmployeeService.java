package com.nhahang.service;

import com.nhahang.dao.EmployeeDAO;
import com.nhahang.model.Employee;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDAO =
            new EmployeeDAO();

    // Lấy danh sách
    public List<Employee> findAll()
            throws SQLException {

        return employeeDAO.findAll();
    }

    // Thêm
    public void insert(
            Employee employee
    ) throws SQLException {

        validate(employee);

        employeeDAO.insert(employee);
    }

    // Sửa
    public void update(
            Employee employee
    ) throws SQLException {

        validate(employee);

        employeeDAO.update(employee);
    }

    // Xóa
    public void delete(int employeeId)
            throws SQLException {

        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mã nhân viên không hợp lệ."
            );
        }

        employeeDAO.delete(employeeId);
    }

    // Kiểm tra dữ liệu
    private void validate(
            Employee employee
    ) {

        if (employee == null) {
            throw new IllegalArgumentException(
                    "Thông tin nhân viên không được để trống."
            );
        }

        if (employee.getId() <= 0) {
            throw new IllegalArgumentException(
                    "Mã nhân viên phải lớn hơn 0."
            );
        }

        if (
                employee.getName() == null
                        || employee.getName().trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Tên nhân viên không được để trống."
            );
        }

    }
}