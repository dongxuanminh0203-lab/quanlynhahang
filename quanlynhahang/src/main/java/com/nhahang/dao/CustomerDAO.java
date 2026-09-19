package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private void ensureTableExists(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS customers ("
                + "customer_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "customer_name VARCHAR(100) NOT NULL, "
                + "phone VARCHAR(20), "
                + "email VARCHAR(100), "
                + "address VARCHAR(255), "
                + "points INT DEFAULT 0, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public List<Customer> findAll() throws SQLException {
        String sql = "SELECT customer_id, customer_name, phone, email, address, points, created_at "
                + "FROM customers ORDER BY customer_id";

        List<Customer> list = new ArrayList<>();

        try (Connection connection = DBHelper.getConnection()) {
            ensureTableExists(connection);

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    list.add(new Customer(
                            resultSet.getInt("customer_id"),
                            resultSet.getString("customer_name"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            resultSet.getString("address"),
                                resultSet.getInt("points"),
                            resultSet.getTimestamp("created_at")
                    ));
                }
            }
        }

        return list;
    }

    public void insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, customer_name, phone, email, address, points) "
            + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBHelper.getConnection()) {
            ensureTableExists(connection);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customer.getId());
                statement.setString(2, customer.getName());
                statement.setString(3, customer.getPhone());
                statement.setString(4, customer.getEmail());
                statement.setString(5, customer.getAddress());
                statement.setInt(6, customer.getPoints());
                statement.executeUpdate();
            }
        }
    }

    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET customer_name = ?, phone = ?, email = ?, address = ?, points = ? "
                + "WHERE customer_id = ?";

        try (Connection connection = DBHelper.getConnection()) {
            ensureTableExists(connection);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, customer.getName());
                statement.setString(2, customer.getPhone());
                statement.setString(3, customer.getEmail());
                statement.setString(4, customer.getAddress());
                statement.setInt(5, customer.getPoints());
                statement.setInt(6, customer.getId());
                statement.executeUpdate();
            }
        }
    }

    public void delete(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection connection = DBHelper.getConnection()) {
            ensureTableExists(connection);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                statement.executeUpdate();
            }
        }
    }

}
