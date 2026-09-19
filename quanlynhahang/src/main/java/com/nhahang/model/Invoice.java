package com.nhahang.model;

public class Invoice {
    private final int orderId;
    private final int tableId;
    private final String tableName;
    private final int employeeId;
    private final String status;
    private final double totalAmount;

    public Invoice(int orderId, int tableId, String tableName, int employeeId,
                   String status, double totalAmount) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.tableName = tableName;
        this.employeeId = employeeId;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public int getOrderId() { return orderId; }
    public int getTableId() { return tableId; }
    public String getTableName() { return tableName; }
    public int getEmployeeId() { return employeeId; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
}
