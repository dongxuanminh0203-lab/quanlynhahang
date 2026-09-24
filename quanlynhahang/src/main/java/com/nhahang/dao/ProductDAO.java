package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.Product;
import com.nhahang.util.AuditLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> findAll() throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, c.category_name, p.price, p.status, p.image_path, p.ingredients, p.serve_immediately "
            + "FROM products p JOIN categories c ON c.category_id = p.category_id "
            + "ORDER BY p.product_id";
        List<Product> records = new ArrayList<>();

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = prepareAfterMigration(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                records.add(new Product(
                        resultSet.getString("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("category_name"),
                        resultSet.getDouble("price"),
                        resultSet.getBoolean("status"),
                        resultSet.getString("image_path"),
                        resultSet.getString("ingredients"),
                        resultSet.getBoolean("serve_immediately")
                ));
            }
        }
        return records;
    }

    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO products "
            + "(product_id, product_name, category_id, price, status, image_path, ingredients, serve_immediately) "
            + "VALUES (?, ?, (SELECT category_id FROM categories WHERE category_name = ?), ?, ?, ?, ?, ?)";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = prepareAfterMigration(connection, sql)) {
            statement.setInt(1, Integer.parseInt(product.getId()));
            statement.setString(2, product.getName());
            statement.setString(3, product.getCategory());
            statement.setDouble(4, product.getPrice());
            statement.setBoolean(5, product.isAvailable());
            statement.setString(6, product.getImagePath());
            statement.setString(7, product.getIngredients());
            statement.setBoolean(8, product.isServeImmediately());
            statement.executeUpdate();
        }
        AuditLogger.log("CREATE_PRODUCT", "PRODUCT", Integer.valueOf(product.getId()), "Tạo món ăn");
    }

    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET product_name = ?, category_id = "
            + "(SELECT category_id FROM categories WHERE category_name = ?), price = ?, "
            + "status = ?, image_path = ?, ingredients = ?, serve_immediately = ? WHERE product_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = prepareAfterMigration(connection, sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setDouble(3, product.getPrice());
            statement.setBoolean(4, product.isAvailable());
            statement.setString(5, product.getImagePath());
            statement.setString(6, product.getIngredients());
            statement.setBoolean(7, product.isServeImmediately());
            statement.setInt(8, Integer.parseInt(product.getId()));
            statement.executeUpdate();
        }
        AuditLogger.log("UPDATE_PRODUCT", "PRODUCT", Integer.valueOf(product.getId()), "Cập nhật món ăn");
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
        AuditLogger.log("DELETE_PRODUCT", "PRODUCT", Integer.valueOf(id), "Xóa món ăn");
    }

    public String nextId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(product_id), 0) + 1 FROM products";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return String.valueOf(resultSet.getInt(1));
        }
    }

    private PreparedStatement prepareAfterMigration(
            Connection connection,
            String sql
    ) throws SQLException {
        ensureImageColumn(connection);
        ensureIngredientsColumn(connection);
        ensureServeImmediatelyColumn(connection);
        return connection.prepareStatement(sql);
    }

    private void ensureServeImmediatelyColumn(Connection connection) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = 'products' "
                + "AND column_name = 'serve_immediately'";
        try (PreparedStatement statement = connection.prepareStatement(checkSql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                try (PreparedStatement alter = connection.prepareStatement(
                        "ALTER TABLE products ADD COLUMN serve_immediately BOOLEAN NOT NULL DEFAULT FALSE")) {
                    alter.executeUpdate();
                }
            }
        }
    }

    private void ensureImageColumn(Connection connection)
            throws SQLException {
        String checkSql =
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() " +
                "AND table_name = 'products' " +
                "AND column_name = 'image_path'";

        try (PreparedStatement statement =
                     connection.prepareStatement(checkSql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            if (resultSet.getInt(1) == 0) {
                try (PreparedStatement alter = connection.prepareStatement(
                        "ALTER TABLE products " +
                                "ADD COLUMN image_path VARCHAR(500) NULL"
                )) {
                    alter.executeUpdate();
                }
            }
        }
    }

    private void ensureIngredientsColumn(Connection connection)
            throws SQLException {
        String checkSql =
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_schema = DATABASE() " +
                "AND table_name = 'products' " +
                "AND column_name = 'ingredients'";

        try (PreparedStatement statement = connection.prepareStatement(checkSql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            if (resultSet.getInt(1) == 0) {
                try (PreparedStatement alter = connection.prepareStatement(
                        "ALTER TABLE products ADD COLUMN ingredients TEXT NULL")) {
                    alter.executeUpdate();
                }
            }
        }
    }

}
