package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.ProductIngredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RecipeDAO {

    public List<ProductIngredient> findByProduct(int productId) throws SQLException {
        try (Connection connection = DBHelper.getConnection()) {
            ensureTable(connection);
            String sql = "SELECT pi.ingredient_id, i.ingredient_name, i.unit, "
                    + "pi.quantity_required FROM product_ingredients pi "
                    + "JOIN ingredients i ON i.ingredient_id = pi.ingredient_id "
                    + "WHERE pi.product_id = ? ORDER BY i.ingredient_name";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, productId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    java.util.ArrayList<ProductIngredient> recipes = new java.util.ArrayList<>();
                    while (resultSet.next()) {
                        recipes.add(new ProductIngredient(
                                resultSet.getInt("ingredient_id"),
                                resultSet.getString("ingredient_name"),
                                resultSet.getString("unit"),
                                resultSet.getDouble("quantity_required")
                        ));
                    }
                    return recipes;
                }
            }
        }
    }

    public void replace(int productId, List<RecipeLine> lines) throws SQLException {
        try (Connection connection = DBHelper.getConnection()) {
            ensureTable(connection);
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement delete = connection.prepareStatement(
                        "DELETE FROM product_ingredients WHERE product_id = ?")) {
                    delete.setInt(1, productId);
                    delete.executeUpdate();
                }
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO product_ingredients "
                                + "(product_id, ingredient_id, quantity_required) VALUES (?, ?, ?)")) {
                    for (RecipeLine line : lines) {
                        insert.setInt(1, productId);
                        insert.setInt(2, line.ingredientId());
                        insert.setDouble(3, line.quantity());
                        insert.addBatch();
                    }
                    insert.executeBatch();
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

    private void ensureTable(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS product_ingredients ("
                        + "product_id INT NOT NULL, ingredient_id INT NOT NULL, "
                        + "quantity_required DECIMAL(12,3) NOT NULL, "
                        + "PRIMARY KEY (product_id, ingredient_id), "
                        + "FOREIGN KEY (product_id) REFERENCES products(product_id), "
                        + "FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id))")) {
            statement.executeUpdate();
        }
    }

    public record RecipeLine(int ingredientId, double quantity) { }
}
