package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.Ingredient;
import com.nhahang.util.AuditLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {

    public List<Ingredient> findAll() throws SQLException {
        try (Connection connection = DBHelper.getConnection()) {
            ensureTable(connection);
            String sql = "SELECT ingredient_id, ingredient_name, unit, stock_quantity, active "
                    + "FROM ingredients ORDER BY ingredient_name";
            List<Ingredient> ingredients = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ingredients.add(new Ingredient(
                            resultSet.getInt("ingredient_id"),
                            resultSet.getString("ingredient_name"),
                            resultSet.getString("unit"),
                            resultSet.getDouble("stock_quantity"),
                            resultSet.getBoolean("active")
                    ));
                }
            }
            return ingredients;
        }
    }

    public void insert(String name, String unit, double stock) throws SQLException {
        ensureTable();
        String sql = "INSERT INTO ingredients (ingredient_name, unit, stock_quantity) VALUES (?, ?, ?)";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, unit);
            statement.setDouble(3, stock);
            statement.executeUpdate();
        }
        AuditLogger.log("CREATE_INGREDIENT", "INGREDIENT", null, "Tạo nguyên liệu: " + name);
    }

    public void update(int id, String name, String unit, double stock, boolean active)
            throws SQLException {
        ensureTable();
        String sql = "UPDATE ingredients SET ingredient_name = ?, unit = ?, "
                + "stock_quantity = ?, active = ? WHERE ingredient_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, unit);
            statement.setDouble(3, stock);
            statement.setBoolean(4, active);
            statement.setInt(5, id);
            statement.executeUpdate();
        }
        AuditLogger.log("UPDATE_INGREDIENT", "INGREDIENT", id, "Cập nhật nguyên liệu: " + name);
    }

    public void delete(int id) throws SQLException {
        ensureTable();
        String sql = "DELETE FROM ingredients WHERE ingredient_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Không thể xóa nguyên liệu đang có trong công thức món ăn");
            }
        }
        AuditLogger.log("DELETE_INGREDIENT", "INGREDIENT", id, "Xóa nguyên liệu");
    }

    private void ensureTable() throws SQLException {
        try (Connection connection = DBHelper.getConnection()) {
            ensureTable(connection);
        }
    }

    private void ensureTable(Connection connection) throws SQLException {
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
    }
}
