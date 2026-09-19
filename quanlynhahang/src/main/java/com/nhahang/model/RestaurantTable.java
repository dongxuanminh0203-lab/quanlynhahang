package com.nhahang.model;

public class RestaurantTable {
    private final int id;
    private final String name;
    private final int capacity;
    private final String status;

    public RestaurantTable(int id, String name, int capacity, String status) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }
}
