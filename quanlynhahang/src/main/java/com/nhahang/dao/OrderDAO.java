package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDAO {

    public int createOrder(int tableId, int employeeId, List<OrderItem> items)
            throws SQLException {
        String orderSql = "INSERT INTO orders "
                + "(table_id, employee_id, status, total_amount) VALUES (?, ?, 'OPEN', ?)";
        String detailSql = "INSERT INTO order_details "
                + "(order_id, product_id, quantity, unit_price, note) VALUES (?, ?, ?, ?, ?)";
        String tableSql = "UPDATE restaurant_tables SET status = 'SERVING' WHERE table_id = ?";

        try (Connection connection = DBHelper.getConnection()) {
            connection.setAutoCommit(false);
            try {
                double total = items.stream()
                        .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                        .sum();
                int orderId;

                try (PreparedStatement statement = connection.prepareStatement(
                        orderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, tableId);
                    statement.setInt(2, employeeId);
                    statement.setDouble(3, total);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Không lấy được mã đơn hàng");
                        }
                        orderId = keys.getInt(1);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(detailSql)) {
                    for (OrderItem item : items) {
                        statement.setInt(1, orderId);
                        statement.setInt(2, item.getProductId());
                        statement.setInt(3, item.getQuantity());
                        statement.setDouble(4, item.getUnitPrice());
                        statement.setString(5, item.getNote());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }

                try (PreparedStatement statement = connection.prepareStatement(tableSql)) {
                    statement.setInt(1, tableId);
                    statement.executeUpdate();
                }

                connection.commit();
                return orderId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public static class OrderItem {
        private final int productId;
        private final int quantity;
        private final double unitPrice;
        private final String note;

        public OrderItem(int productId, int quantity, double unitPrice, String note) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.note = note;
        }

        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public String getNote() { return note; }
    }
}
