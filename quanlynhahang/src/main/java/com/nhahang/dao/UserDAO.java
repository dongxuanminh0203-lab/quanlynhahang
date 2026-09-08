package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

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
}