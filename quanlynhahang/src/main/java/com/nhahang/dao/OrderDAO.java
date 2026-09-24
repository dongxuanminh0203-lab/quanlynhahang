package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.OrderItem;
import com.nhahang.util.AuditLogger;

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
            + "(order_id, product_id, quantity, unit_price, note, cooking_status) "
            + "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        String tableSql = "UPDATE restaurant_tables SET status = 'SERVING' WHERE table_id = ?";

        try (Connection connection = DBHelper.getConnection()) {
            ensureCookingStatusColumn(connection);
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
                AuditLogger.log("CREATE_ORDER", "ORDER", orderId, "Tạo đơn hàng cho bàn " + tableId);
                return orderId;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void ensureCookingStatusColumn(Connection connection)
            throws SQLException {
        String checkSql =
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() " +
                "AND table_name = 'order_details' " +
                "AND column_name = 'cooking_status'";

        try (PreparedStatement statement = connection.prepareStatement(checkSql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                try (PreparedStatement alter = connection.prepareStatement(
                        "ALTER TABLE order_details " +
                                "ADD COLUMN cooking_status VARCHAR(20) NOT NULL DEFAULT 'PENDING'")) {
                    alter.executeUpdate();
                }
            }
        }
    }

}
