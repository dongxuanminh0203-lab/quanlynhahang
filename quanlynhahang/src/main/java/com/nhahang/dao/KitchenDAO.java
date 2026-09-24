package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.KitchenOrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KitchenDAO {

    public List<KitchenOrderItem> findItems() throws SQLException {
        try (Connection connection = DBHelper.getConnection()) {
            ensureInventorySchema(connection);
            String sql = "SELECT o.order_id, od.product_id, rt.table_name, "
                    + "p.product_name, od.quantity, p.ingredients, od.note, od.cooking_status "
                    + "FROM order_details od "
                    + "JOIN orders o ON o.order_id = od.order_id "
                    + "JOIN products p ON p.product_id = od.product_id "
                    + "JOIN restaurant_tables rt ON rt.table_id = o.table_id "
                    + "WHERE o.status <> 'PAID' "
                    + "ORDER BY od.cooking_status, o.order_id, od.product_id";
            List<KitchenOrderItem> items = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new KitchenOrderItem(
                            resultSet.getInt("order_id"),
                            resultSet.getInt("product_id"),
                            resultSet.getString("table_name"),
                            resultSet.getString("product_name"),
                            resultSet.getInt("quantity"),
                            resultSet.getString("ingredients"),
                            resultSet.getString("note"),
                            resultSet.getString("cooking_status")
                    ));
                }
            }
            return items;
        }
    }

    public void updateStatus(int orderId, int productId, String status) throws SQLException {
        try (Connection connection = DBHelper.getConnection()) {
            ensureInventorySchema(connection);
            connection.setAutoCommit(false);
            try {
                if ("READY".equals(status)) {
                    deductIngredients(connection, orderId, productId);
                }

                String sql = "UPDATE order_details SET cooking_status = ? "
                        + "WHERE order_id = ? AND product_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, status);
                    statement.setInt(2, orderId);
                    statement.setInt(3, productId);
                    statement.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void deductIngredients(Connection connection, int orderId, int productId)
            throws SQLException {
        String detailSql = "SELECT quantity, inventory_deducted_at FROM order_details "
                + "WHERE order_id = ? AND product_id = ? FOR UPDATE";
        int orderQuantity;
        boolean alreadyDeducted;
        try (PreparedStatement statement = connection.prepareStatement(detailSql)) {
            statement.setInt(1, orderId);
            statement.setInt(2, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Không tìm thấy món trong đơn hàng");
                }
                orderQuantity = resultSet.getInt("quantity");
                alreadyDeducted = resultSet.getTimestamp("inventory_deducted_at") != null;
            }
        }

        if (alreadyDeducted) {
            return;
        }

        String recipeSql = "SELECT pi.ingredient_id, pi.quantity_required, "
                + "i.ingredient_name, i.stock_quantity "
                + "FROM product_ingredients pi JOIN ingredients i "
                + "ON i.ingredient_id = pi.ingredient_id "
                + "WHERE pi.product_id = ? FOR UPDATE";
        List<IngredientUsage> usages = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(recipeSql)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    double required = resultSet.getDouble("quantity_required") * orderQuantity;
                    double stock = resultSet.getDouble("stock_quantity");
                    if (stock < required) {
                        throw new SQLException("Không đủ nguyên liệu: "
                                + resultSet.getString("ingredient_name")
                                + " (còn " + stock + ", cần " + required + ")");
                    }
                    usages.add(new IngredientUsage(
                            resultSet.getInt("ingredient_id"), required));
                }
            }
        }

        if (usages.isEmpty()) {
            throw new SQLException("Món này chưa được khai báo công thức nguyên liệu");
        }

        String updateStockSql = "UPDATE ingredients SET stock_quantity = stock_quantity - ? "
                + "WHERE ingredient_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateStockSql)) {
            for (IngredientUsage usage : usages) {
                statement.setDouble(1, usage.quantity);
                statement.setInt(2, usage.ingredientId);
                statement.addBatch();
            }
            statement.executeBatch();
        }

        String markSql = "UPDATE order_details SET inventory_deducted_at = CURRENT_TIMESTAMP "
                + "WHERE order_id = ? AND product_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(markSql)) {
            statement.setInt(1, orderId);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }
    }

    private void ensureInventorySchema(Connection connection) throws SQLException {
        ensureCookingStatusColumn(connection);
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS ingredients ("
                        + "ingredient_id INT AUTO_INCREMENT PRIMARY KEY, "
                        + "ingredient_name VARCHAR(100) NOT NULL UNIQUE, "
                        + "unit VARCHAR(20) NOT NULL, "
                        + "stock_quantity DECIMAL(12,3) NOT NULL DEFAULT 0, "
                        + "active BOOLEAN NOT NULL DEFAULT TRUE, "
                        + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                        + "ON UPDATE CURRENT_TIMESTAMP)")) {
            statement.executeUpdate();
        }
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS product_ingredients ("
                        + "product_id INT NOT NULL, ingredient_id INT NOT NULL, "
                        + "quantity_required DECIMAL(12,3) NOT NULL, "
                        + "PRIMARY KEY (product_id, ingredient_id), "
                        + "FOREIGN KEY (product_id) REFERENCES products(product_id), "
                        + "FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id))")) {
            statement.executeUpdate();
        }
        addColumnIfMissing(connection, "order_details", "inventory_deducted_at",
                "DATETIME NULL");
    }

    private void addColumnIfMissing(Connection connection, String tableName,
                                    String columnName, String definition) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setString(1, tableName);
            check.setString(2, columnName);
            try (ResultSet resultSet = check.executeQuery()) {
                resultSet.next();
                if (resultSet.getInt(1) == 0) {
                    try (PreparedStatement alter = connection.prepareStatement(
                            "ALTER TABLE " + tableName + " ADD COLUMN " + columnName
                                    + " " + definition)) {
                        alter.executeUpdate();
                    }
                }
            }
        }
    }

    private void ensureCookingStatusColumn(Connection connection) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = 'order_details' "
                + "AND column_name = 'cooking_status'";
        try (PreparedStatement statement = connection.prepareStatement(checkSql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                try (PreparedStatement alter = connection.prepareStatement(
                        "ALTER TABLE order_details ADD COLUMN cooking_status "
                                + "VARCHAR(20) NOT NULL DEFAULT 'PENDING'")) {
                    alter.executeUpdate();
                }
            }
        }
    }

    private static class IngredientUsage {
        private final int ingredientId;
        private final double quantity;

        private IngredientUsage(int ingredientId, double quantity) {
            this.ingredientId = ingredientId;
            this.quantity = quantity;
        }
    }
}
