package com.nhahang.controller;

import com.nhahang.dao.UserDAO;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    private final UserDAO userDAO = new UserDAO();

    public List<UserDAO.UserRecord> loadUsers() throws SQLException {
        return userDAO.findAll();
    }

    public void addUser(UserDAO.UserRecord user) throws SQLException {
        validate(user, true);
        userDAO.insert(user);
    }

    public void updateUser(UserDAO.UserRecord user) throws SQLException {
        validate(user, false);
        userDAO.update(user);
    }

    public void deleteUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Mã tài khoản không hợp lệ.");
        }
        userDAO.delete(userId);
    }

    private void validate(UserDAO.UserRecord user, boolean creating) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên tài khoản không được để trống.");
        }
        if (creating && (user.getPassword() == null || user.getPassword().isEmpty())) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò không được để trống.");
        }
    }
}
