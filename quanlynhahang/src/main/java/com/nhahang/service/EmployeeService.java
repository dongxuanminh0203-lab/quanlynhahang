package com.nhahang.service;

import com.nhahang.dao.EmployeeDAO;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private final EmployeeDAO employeeDAO =
            new EmployeeDAO();

    // Lấy danh sách
    public List<EmployeeDAO.EmployeeRecord> findAll()
            throws SQLException {

        return employeeDAO.findAll();
    }

    // Thêm
    public void insert(
            EmployeeDAO.EmployeeRecord employee
    ) throws SQLException {

        validate(employee);

        employeeDAO.insert(employee);
    }

    // Sửa
    public void update(
            EmployeeDAO.EmployeeRecord employee
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
            EmployeeDAO.EmployeeRecord employee
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

        if (employee.getSalary() < 0) {
            throw new IllegalArgumentException(
                    "Lương không được âm."
            );
        }
    }
}