package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.OrderItem;
import com.nhahang.util.AuditLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class OrderDAO {

    public int createOrder(int tableId, int employeeId, List<OrderItem> items)
            throws SQLException {
        String orderSql = "INSERT INTO orders "
                + "(table_id, employee_id, status, total_amount) VALUES (?, ?, 'OPEN', ?)";
        String existingOrderSql = "SELECT order_id FROM orders "
            + "WHERE table_id = ? AND status = 'OPEN' "
            + "ORDER BY order_id DESC LIMIT 1 FOR UPDATE";
        String detailSql = "INSERT INTO order_details "
            + "(order_id, product_id, quantity, unit_price, note, cooking_status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String mergeDetailSql = "UPDATE order_details SET quantity = quantity + ? "
            + "WHERE order_id = ? AND product_id = ? "
            + "AND cooking_status IN ('PENDING', 'COOKING') "
            + "AND inventory_deducted_at IS NULL LIMIT 1";
        String totalSql = "UPDATE orders SET total_amount = total_amount + ? "
            + "WHERE order_id = ? AND status = 'OPEN'";
        String tableSql = "UPDATE restaurant_tables SET status = 'SERVING' WHERE table_id = ?";

        try (Connection connection = DBHelper.getConnection()) {
            ensureCookingStatusColumn(connection);
            ensureInventorySchema(connection);
            connection.setAutoCommit(false);
            try {
                double total = items.stream()
                        .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                        .sum();
                int orderId;

                try (PreparedStatement statement = connection.prepareStatement(existingOrderSql)) {
                    statement.setInt(1, tableId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            orderId = resultSet.getInt("order_id");
                            try (PreparedStatement update = connection.prepareStatement(totalSql)) {
                                update.setDouble(1, total);
                                update.setInt(2, orderId);
                                update.executeUpdate();
                            }
                        } else {
                            try (PreparedStatement insert = connection.prepareStatement(
                                    orderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                                insert.setInt(1, tableId);
                                insert.setInt(2, employeeId);
                                insert.setDouble(3, total);
                                insert.executeUpdate();
                                try (ResultSet keys = insert.getGeneratedKeys()) {
                                    if (!keys.next()) {
                                        throw new SQLException("Không lấy được mã đơn hàng");
                                    }
                                    orderId = keys.getInt(1);
                                }
                            }
                        }
                    }
                }

                try (PreparedStatement merge = connection.prepareStatement(mergeDetailSql);
                     PreparedStatement insert = connection.prepareStatement(detailSql)) {
                    for (OrderItem item : items) {
                        int updated = 0;
                        if (!item.isServeImmediately()) {
                            merge.setInt(1, item.getQuantity());
                            merge.setInt(2, orderId);
                            merge.setInt(3, item.getProductId());
                            updated = merge.executeUpdate();
                        }

                        if (updated == 0) {
                            insert.setInt(1, orderId);
                            insert.setInt(2, item.getProductId());
                            insert.setInt(3, item.getQuantity());
                            insert.setDouble(4, item.getUnitPrice());
                            insert.setString(5, item.getNote());
                            insert.setString(6, item.isServeImmediately() ? "READY" : "PENDING");
                            insert.executeUpdate();
                            if (item.isServeImmediately()) {
                                deductImmediateIngredients(
                                    connection,
                                    orderId,
                                    item.getProductId(),
                                    item.getQuantity()
                                );
                            }
                        }
                    }
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

    private void deductImmediateIngredients(Connection connection, int orderId, int productId,
                                            int orderQuantity)
            throws SQLException {
        String recipeSql = "SELECT pi.ingredient_id, pi.quantity_required, "
                + "i.ingredient_name, i.stock_quantity FROM product_ingredients pi "
                + "JOIN ingredients i ON i.ingredient_id = pi.ingredient_id "
                + "WHERE pi.product_id = ? FOR UPDATE";
        List<Integer> ids = new ArrayList<>();
        List<Double> quantities = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(recipeSql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ids.add(resultSet.getInt("ingredient_id"));
                    double required = resultSet.getDouble("quantity_required") * orderQuantity;
                    quantities.add(required);
                    if (resultSet.getDouble("stock_quantity") < required) {
                        throw new SQLException("Không đủ nguyên liệu: " + resultSet.getString("ingredient_name"));
                    }
                }
            }
        }
        if (ids.isEmpty()) return;
        try (PreparedStatement update = connection.prepareStatement(
                "UPDATE ingredients SET stock_quantity = stock_quantity - ? WHERE ingredient_id = ?")) {
            for (int index = 0; index < ids.size(); index++) {
                update.setDouble(1, quantities.get(index));
                update.setInt(2, ids.get(index));
                update.addBatch();
            }
            update.executeBatch();
        }
        try (PreparedStatement mark = connection.prepareStatement(
                "UPDATE order_details SET inventory_deducted_at = CURRENT_TIMESTAMP "
                        + "WHERE order_id = ? AND product_id = ? AND inventory_deducted_at IS NULL")) {
            mark.setInt(1, orderId);
            mark.setInt(2, productId);
            mark.executeUpdate();
        }
    }

    private void ensureInventorySchema(Connection connection) throws SQLException {
        try (PreparedStatement ingredients = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ingredients (ingredient_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "ingredient_name VARCHAR(100) NOT NULL UNIQUE, unit VARCHAR(20) NOT NULL, "
                        + "stock_quantity DECIMAL(12,3) NOT NULL DEFAULT 0, active BOOLEAN NOT NULL DEFAULT TRUE)")) {
            ingredients.executeUpdate();
        }
        try (PreparedStatement recipes = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS product_ingredients (product_id INT NOT NULL, "
                        + "ingredient_id INT NOT NULL, quantity_required DECIMAL(12,3) NOT NULL, "
                        + "PRIMARY KEY (product_id, ingredient_id))")) {
            recipes.executeUpdate();
        }
        String check = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() "
                + "AND table_name = 'order_details' AND column_name = 'inventory_deducted_at'";
        try (PreparedStatement statement = connection.prepareStatement(check);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                try (PreparedStatement alter = connection.prepareStatement(
                        "ALTER TABLE order_details ADD COLUMN inventory_deducted_at DATETIME NULL")) {
                    alter.executeUpdate();
                }
            }
        }
    }

}
