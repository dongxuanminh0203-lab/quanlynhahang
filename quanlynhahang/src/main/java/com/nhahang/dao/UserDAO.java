package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

        public List<UserRecord> findAll() throws SQLException {
                String sql = "SELECT u.user_id, u.username, u.password, u.role, u.employee_id, "
                                + "u.status, u.created_at, e.employee_name "
                                + "FROM users u LEFT JOIN employees e ON e.employee_id = u.employee_id "
                                + "ORDER BY u.user_id";
                List<UserRecord> users = new ArrayList<>();

                try (Connection connection = DBHelper.getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql);
                         ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                                users.add(new UserRecord(
                                                resultSet.getInt("user_id"),
                                                resultSet.getString("username"),
                                                resultSet.getString("password"),
                                                resultSet.getString("role"),
                                                resultSet.getObject("employee_id", Integer.class),
                                                resultSet.getBoolean("status"),
                                                resultSet.getTimestamp("created_at"),
                                                resultSet.getString("employee_name")
                                ));
                        }
                }
                return users;
        }

        public void insert(UserRecord user) throws SQLException {
                String sql = "INSERT INTO users (username, password, role, employee_id, status) "
                                + "VALUES (?, ?, ?, ?, ?)";
                try (Connection connection = DBHelper.getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, user.getUsername());
                        statement.setString(2, user.getPassword());
                        statement.setString(3, user.getRole());
                        setNullableEmployeeId(statement, 4, user.getEmployeeId());
                        statement.setBoolean(5, user.isActive());
                        statement.executeUpdate();
                }
        }

        public void update(UserRecord user) throws SQLException {
                String sql;
                if (user.getPassword() == null || user.getPassword().isEmpty()) {
                        sql = "UPDATE users SET username = ?, role = ?, employee_id = ?, status = ? "
                                        + "WHERE user_id = ?";
                } else {
                        sql = "UPDATE users SET username = ?, password = ?, role = ?, employee_id = ?, status = ? "
                                        + "WHERE user_id = ?";
                }

                try (Connection connection = DBHelper.getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {
                        int index = 1;
                        statement.setString(index++, user.getUsername());
                        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                                statement.setString(index++, user.getPassword());
                        }
                        statement.setString(index++, user.getRole());
                        setNullableEmployeeId(statement, index++, user.getEmployeeId());
                        statement.setBoolean(index++, user.isActive());
                        statement.setInt(index, user.getId());
                        statement.executeUpdate();
                }
        }

        public void delete(int userId) throws SQLException {
                String sql = "DELETE FROM users WHERE user_id = ?";
                try (Connection connection = DBHelper.getConnection();
                         PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setInt(1, userId);
                        statement.executeUpdate();
                }
        }

        private void setNullableEmployeeId(PreparedStatement statement, int index, Integer employeeId)
                        throws SQLException {
                if (employeeId == null) {
                        statement.setNull(index, java.sql.Types.INTEGER);
                } else {
                        statement.setInt(index, employeeId);
                }
        }

    public User login(String username, String password) {

        String sql = """
                SELECT user_id,
                       username,
                       password,
                       role,
                       employee_id,
                       status
                FROM users
                WHERE username = ?
                  AND password = ?
                  AND status = 1
                """;

        try (
                Connection conn = DBHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = new User();

                    user.setUserId(
                            rs.getInt("user_id")
                    );

                    user.setUsername(
                            rs.getString("username")
                    );

                    user.setPassword(
                            rs.getString("password")
                    );

                    user.setRole(
                            rs.getString("role")
                    );

                    user.setEmployeeId(
                            rs.getInt("employee_id")
                    );

                    user.setStatus(
                            rs.getBoolean("status")
                    );

                    return user;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

        public static class UserRecord {
                private final int id;
                private final String username;
                private final String password;
                private final String role;
                private final Integer employeeId;
                private final boolean active;
                private final Timestamp createdAt;
                private final String employeeName;

                public UserRecord(int id, String username, String password, String role,
                                                  Integer employeeId, boolean active, Timestamp createdAt,
                                                  String employeeName) {
                        this.id = id;
                        this.username = username;
                        this.password = password;
                        this.role = role;
                        this.employeeId = employeeId;
                        this.active = active;
                        this.createdAt = createdAt;
                        this.employeeName = employeeName;
                }

                public int getId() { return id; }
                public String getUsername() { return username; }
                public String getPassword() { return password; }
                public String getRole() { return role; }
                public Integer getEmployeeId() { return employeeId; }
                public boolean isActive() { return active; }
                public Timestamp getCreatedAt() { return createdAt; }
                public String getEmployeeName() { return employeeName; }
        }
}