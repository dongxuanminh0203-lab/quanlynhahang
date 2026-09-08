package com.nhahang.model;

public class User {

    private int userId;
    private String username;
    private String password;
    private String role;
    private int employeeId;
    private boolean status;

    public User() {
    }

    public User(int userId, String username, String password,
                String role, int employeeId, boolean status) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", employeeId=" + employeeId +
                ", status=" + status +
                '}';
    }
}