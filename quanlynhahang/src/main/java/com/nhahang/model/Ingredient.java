package com.nhahang.model;

public class Ingredient {
    private final int id;
    private final String name;
    private final String unit;
    private final double stockQuantity;
    private final boolean active;

    public Ingredient(int id, String name, String unit, double stockQuantity, boolean active) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.active = active;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUnit() { return unit; }
    public double getStockQuantity() { return stockQuantity; }
    public boolean isActive() { return active; }
}
