package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.User;
import com.nhahang.model.UserRecord;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            statement.setString(2, hashPassword(user.getPassword()));
            statement.setString(3, normalizeRole(user.getRole()));
            setNullableEmployeeId(statement, 4, user.getEmployeeId());
            statement.setBoolean(5, user.isActive());
            statement.executeUpdate();

            logAudit("CREATE_USER", "USER", null, user.getUsername(), "Tạo tài khoản mới");
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
                statement.setString(index++, hashPassword(user.getPassword()));
            }
            statement.setString(index++, normalizeRole(user.getRole()));
            setNullableEmployeeId(statement, index++, user.getEmployeeId());
            statement.setBoolean(index++, user.isActive());
            statement.setInt(index, user.getId());
            statement.executeUpdate();

            logAudit("UPDATE_USER", "USER", user.getId(), user.getUsername(), "Cập nhật thông tin tài khoản");
        }
    }

    public void delete(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }

        logAudit("DELETE_USER", "USER", userId, "system", "Xóa tài khoản");
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
        String sql = "SELECT user_id, username, password, role, employee_id, status FROM users WHERE username = ? AND status = 1";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                        User user = new User();
                        user.setUserId(rs.getInt("user_id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(storedHash);
                        user.setRole(rs.getString("role"));
                        user.setEmployeeId(rs.getInt("employee_id"));
                        user.setStatus(rs.getBoolean("status"));

                        logAudit("LOGIN_SUCCESS", "USER", user.getUserId(), username, "Đăng nhập thành công");
                        return user;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        logAudit("LOGIN_FAILED", "USER", null, username, "Đăng nhập thất bại");
        return null;
    }

    private void logAudit(String actionName, String entityName, Integer entityId, String actorUsername, String details) {
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO audit_logs (action_name, entity_name, entity_id, actor_username, details) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, actionName);
            statement.setString(2, entityName);
            if (entityId == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, entityId);
            }
            statement.setString(4, actorUsername);
            statement.setString(5, details);
            statement.executeUpdate();
        } catch (SQLException ignored) {
            // no-op to avoid breaking business flow
        }
    }

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            return null;
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "STAFF";
        }
        String value = role.trim().toUpperCase();
        if ("ADMIN".equals(value) || "STAFF".equals(value)) {
            return value;
        }
        return "STAFF";
    }
}