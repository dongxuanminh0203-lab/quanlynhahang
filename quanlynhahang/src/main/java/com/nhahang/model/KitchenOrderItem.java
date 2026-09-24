package com.nhahang.model;

public class KitchenOrderItem {
    private final int orderId;
    private final int productId;
    private final String tableName;
    private final String productName;
    private final int quantity;
    private final String ingredients;
    private final String note;
    private String cookingStatus;

    public KitchenOrderItem(int orderId, int productId, String tableName,
                            String productName, int quantity, String ingredients,
                            String note, String cookingStatus) {
        this.orderId = orderId;
        this.productId = productId;
        this.tableName = tableName;
        this.productName = productName;
        this.quantity = quantity;
        this.ingredients = ingredients == null ? "" : ingredients;
        this.note = note == null ? "" : note;
        this.cookingStatus = cookingStatus;
    }

    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public String getTableName() { return tableName; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public String getIngredients() { return ingredients; }
    public String getNote() { return note; }
    public String getCookingStatus() { return cookingStatus; }
    public void setCookingStatus(String cookingStatus) { this.cookingStatus = cookingStatus; }
}
