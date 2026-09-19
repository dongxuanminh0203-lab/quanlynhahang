package com.nhahang.model;

import java.sql.Timestamp;

public class Payment {
    private final int paymentId;
    private final int orderId;
    private final Timestamp paymentDate;
    private final double amount;
    private final String paymentMethod;

    public Payment(int paymentId, int orderId, Timestamp paymentDate,
                   double amount, String paymentMethod) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public int getPaymentId() { return paymentId; }
    public int getOrderId() { return orderId; }
    public Timestamp getPaymentDate() { return paymentDate; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
}
