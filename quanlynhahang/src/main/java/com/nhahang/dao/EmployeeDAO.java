package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // =========================
    // LẤY TẤT CẢ NHÂN VIÊN
    // =========================

    public List<EmployeeRecord> findAll() throws SQLException {

        String sql =
                "SELECT employee_id, employee_name, gender, phone, " +
                "email, address, position, status, created_at " +
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
                                resultSet.getString("gender"),
                                resultSet.getString("phone"),
                                resultSet.getString("email"),
                                resultSet.getString("address"),
                                resultSet.getString("position"),
                                resultSet.getBoolean("status"),
                                resultSet.getTimestamp("created_at")
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
                "(employee_id, employee_name, gender, phone, " +
                "email, address, position, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
                    employee.getGender()
            );

            statement.setString(
                    4,
                    employee.getPhone()
            );

            statement.setString(
                    5,
                    employee.getEmail()
            );

            statement.setString(
                    6,
                    employee.getAddress()
            );

            statement.setString(
                    7,
                    employee.getPosition()
            );

            statement.setBoolean(
                    8,
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
                "gender = ?, " +
                "phone = ?, " +
                "email = ?, " +
                "address = ?, " +
                "position = ?, " +
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
                    employee.getGender()
            );

            statement.setString(
                    3,
                    employee.getPhone()
            );

            statement.setString(
                    4,
                    employee.getEmail()
            );

            statement.setString(
                    5,
                    employee.getAddress()
            );

            statement.setString(
                    6,
                    employee.getPosition()
            );

            statement.setBoolean(
                    7,
                    employee.isActive()
            );

            statement.setInt(
                    8,
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
        private final String gender;
        private final String phone;
        private final String email;
        private final String address;
        private final String position;
        private final boolean active;
        private final Timestamp createdAt;

        public EmployeeRecord(
                int id,
                String name,
                String gender,
                String phone,
                String email,
                String address,
                String position,
                boolean active,
                Timestamp createdAt
        ) {

            this.id = id;
            this.name = name;
            this.gender = gender;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.position = position;
            this.active = active;
            this.createdAt = createdAt;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

                public String getGender() {
                        return gender;
                }

        public String getPhone() {
            return phone;
        }

                public String getEmail() {
                        return email;
                }

                public String getAddress() {
                        return address;
                }

        public String getPosition() {
            return position;
        }

        public boolean isActive() {
            return active;
        }

                public Timestamp getCreatedAt() {
                        return createdAt;
                }
    }
}