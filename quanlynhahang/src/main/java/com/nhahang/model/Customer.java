package com.nhahang.model;

import java.sql.Timestamp;

public class Customer {
    private final int id;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final int points;
    private final Timestamp createdAt;

    public Customer(int id, String name, String phone, String email, String address,
                    int points, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.points = points;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public int getPoints() { return points; }
    public Timestamp getCreatedAt() { return createdAt; }
}
