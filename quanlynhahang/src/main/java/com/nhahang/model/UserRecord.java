package com.nhahang.model;

import java.sql.Timestamp;

public class UserRecord {
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
