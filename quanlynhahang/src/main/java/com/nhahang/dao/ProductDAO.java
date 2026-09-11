package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<ProductRecord> findAll() throws SQLException {
        String sql = "SELECT p.product_id, p.product_name, c.category_name, p.price, p.status, p.image_path "
            + "FROM products p JOIN categories c ON c.category_id = p.category_id "
            + "ORDER BY p.product_id";
        List<ProductRecord> records = new ArrayList<>();

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = prepareAfterMigration(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                records.add(new ProductRecord(
                        resultSet.getString("product_id"),
                        resultSet.getString("product_name"),
                        resultSet.getString("category_name"),
                        resultSet.getDouble("price"),
                        resultSet.getBoolean("status"),
                        resultSet.getString("image_path")
                ));
            }
        }
        return records;
    }

    public void insert(ProductRecord product) throws SQLException {
        String sql = "INSERT INTO products "
            + "(product_id, product_name, category_id, price, status, image_path) "
            + "VALUES (?, ?, (SELECT category_id FROM categories WHERE category_name = ?), ?, ?, ?)";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = prepareAfterMigration(connection, sql)) {
            statement.setInt(1, Integer.parseInt(product.getId()));
            statement.setString(2, product.getName());
            statement.setString(3, product.getCategory());
            statement.setDouble(4, product.getPrice());
            statement.setBoolean(5, product.isAvailable());
            statement.setString(6, product.getImagePath());
            statement.executeUpdate();
        }
    }

    public void update(ProductRecord product) throws SQLException {
        String sql = "UPDATE products SET product_name = ?, category_id = "
            + "(SELECT category_id FROM categories WHERE category_name = ?), price = ?, "
            + "status = ?, image_path = ? WHERE product_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = prepareAfterMigration(connection, sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setDouble(3, product.getPrice());
            statement.setBoolean(4, product.isAvailable());
            statement.setString(5, product.getImagePath());
            statement.setInt(6, Integer.parseInt(product.getId()));
            statement.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
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
        return connection.prepareStatement(sql);
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

    public static class ProductRecord {
        private final String id;
        private final String name;
        private final String category;
        private final double price;
        private final boolean available;
        private final String imagePath;

        public ProductRecord(String id, String name, String category, double price,
                             boolean available, String imagePath) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.available = available;
            this.imagePath = imagePath;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public boolean isAvailable() { return available; }
        public String getImagePath() { return imagePath; }
    }
}
