package com.nhahang.model;

public class InvoiceDetail {
    private final int productId;
    private final String productName;
    private final int quantity;
    private final double unitPrice;
    private final String note;

    public InvoiceDetail(int productId, String productName, int quantity,
                         double unitPrice, String note) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.note = note;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getNote() { return note; }
    public double getAmount() { return quantity * unitPrice; }
}
