package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // =========================
    // LẤY TẤT CẢ NHÂN VIÊN
    // =========================

    public List<EmployeeRecord> findAll() throws SQLException {

        String sql =
                "SELECT employee_id, employee_name, phone, " +
                "position, salary, status " +
                "FROM employees " +
                "ORDER BY employee_id";

        List<EmployeeRecord> list =
                new ArrayList<>();

        try (
                Connection connection =
                        DBHelper.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                list.add(
                        new EmployeeRecord(
                                resultSet.getInt("employee_id"),
                                resultSet.getString("employee_name"),
                                resultSet.getString("phone"),
                                resultSet.getString("position"),
                                resultSet.getDouble("salary"),
                                resultSet.getBoolean("status")
                        )
                );
            }
        }

        return list;
    }

    // =========================
    // THÊM
    // =========================

    public void insert(EmployeeRecord employee)
            throws SQLException {

        String sql =
                "INSERT INTO employees " +
                "(employee_id, employee_name, phone, " +
                "position, salary, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DBHelper.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    employee.getId()
            );

            statement.setString(
                    2,
                    employee.getName()
            );

            statement.setString(
                    3,
                    employee.getPhone()
            );

            statement.setString(
                    4,
                    employee.getPosition()
            );

            statement.setDouble(
                    5,
                    employee.getSalary()
            );

            statement.setBoolean(
                    6,
                    employee.isActive()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // SỬA
    // =========================

    public void update(EmployeeRecord employee)
            throws SQLException {

        String sql =
                "UPDATE employees SET " +
                "employee_name = ?, " +
                "phone = ?, " +
                "position = ?, " +
                "salary = ?, " +
                "status = ? " +
                "WHERE employee_id = ?";

        try (
                Connection connection =
                        DBHelper.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    employee.getName()
            );

            statement.setString(
                    2,
                    employee.getPhone()
            );

            statement.setString(
                    3,
                    employee.getPosition()
            );

            statement.setDouble(
                    4,
                    employee.getSalary()
            );

            statement.setBoolean(
                    5,
                    employee.isActive()
            );

            statement.setInt(
                    6,
                    employee.getId()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // XÓA
    // =========================

    public void delete(int employeeId)
            throws SQLException {

        String sql =
                "DELETE FROM employees " +
                "WHERE employee_id = ?";

        try (
                Connection connection =
                        DBHelper.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    employeeId
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // MODEL
    // =========================

    public static class EmployeeRecord {

        private final int id;
        private final String name;
        private final String phone;
        private final String position;
        private final double salary;
        private final boolean active;

        public EmployeeRecord(
                int id,
                String name,
                String phone,
                String position,
                double salary,
                boolean active
        ) {

            this.id = id;
            this.name = name;
            this.phone = phone;
            this.position = position;
            this.salary = salary;
            this.active = active;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getPosition() {
            return position;
        }

        public double getSalary() {
            return salary;
        }

        public boolean isActive() {
            return active;
        }
    }
}