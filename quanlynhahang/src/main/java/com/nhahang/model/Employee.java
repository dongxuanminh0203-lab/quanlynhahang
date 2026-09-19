package com.nhahang.model;

import java.sql.Timestamp;

public class Employee {
    private final int id;
    private final String name;
    private final String gender;
    private final String phone;
    private final String email;
    private final String address;
    private final String position;
    private final boolean active;
    private final Timestamp createdAt;

    public Employee(int id, String name, String gender, String phone, String email,
                    String address, String position, boolean active, Timestamp createdAt) {
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

    public int getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPosition() { return position; }
    public boolean isActive() { return active; }
    public Timestamp getCreatedAt() { return createdAt; }
}
