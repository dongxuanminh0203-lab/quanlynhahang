package com.nhahang.model;

public class OrderItem {
    private final int productId;
    private final int quantity;
    private final double unitPrice;
    private final String note;
    private final boolean serveImmediately;

    public OrderItem(int productId, int quantity, double unitPrice, String note) {
        this(productId, quantity, unitPrice, note, false);
    }

    public OrderItem(int productId, int quantity, double unitPrice, String note,
                     boolean serveImmediately) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.note = note;
        this.serveImmediately = serveImmediately;
    }

    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getNote() { return note; }
    public boolean isServeImmediately() { return serveImmediately; }
}
