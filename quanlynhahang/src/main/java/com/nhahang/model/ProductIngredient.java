package com.nhahang.model;

public class ProductIngredient {
    private final int ingredientId;
    private final String ingredientName;
    private final String unit;
    private final double quantityRequired;

    public ProductIngredient(int ingredientId, String ingredientName, String unit,
                             double quantityRequired) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.unit = unit;
        this.quantityRequired = quantityRequired;
    }

    public int getIngredientId() { return ingredientId; }
    public String getIngredientName() { return ingredientName; }
    public String getUnit() { return unit; }
    public double getQuantityRequired() { return quantityRequired; }
}
