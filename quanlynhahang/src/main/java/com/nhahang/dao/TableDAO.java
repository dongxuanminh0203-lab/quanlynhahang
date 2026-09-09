package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    public List<TableRecord> findAll() throws SQLException {
        String sql = "SELECT table_id, table_name, capacity, status "
            + "FROM restaurant_tables ORDER BY table_id";
        List<TableRecord> records = new ArrayList<>();

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                records.add(new TableRecord(
                        resultSet.getInt("table_id"),
                        resultSet.getString("table_name"),
                        resultSet.getInt("capacity"),
                    toDisplayStatus(resultSet.getString("status"))
                ));
            }
        }
        return records;
    }

    public int countAll() throws SQLException {
        return count("SELECT COUNT(*) FROM restaurant_tables");
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM restaurant_tables WHERE status = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    public int insert(String name, int capacity) throws SQLException {
        String sql = "INSERT INTO restaurant_tables (table_name, capacity, status) "
                + "VALUES (?, ?, 'EMPTY')";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, capacity);
            return statement.executeUpdate();
        }
    }

    public int update(int id, String name, int capacity) throws SQLException {
        String sql = "UPDATE restaurant_tables SET table_name = ?, capacity = ? "
                + "WHERE table_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, capacity);
            statement.setInt(3, id);
            return statement.executeUpdate();
        }
    }

    public int updateStatus(int id, boolean serving) throws SQLException {
        String sql = "UPDATE restaurant_tables SET status = ? WHERE table_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serving ? "SERVING" : "EMPTY");
            statement.setInt(2, id);
            return statement.executeUpdate();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM restaurant_tables WHERE table_id = ?";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate();
        }
    }

    private String toDisplayStatus(String status) {
        return "EMPTY".equalsIgnoreCase(status)
                ? "TRỐNG"
                : "ĐANG PHỤC VỤ";
    }

    private int count(String sql) throws SQLException {
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    public static class TableRecord {
        private final int id;
        private final String name;
        private final int capacity;
        private final String status;

        public TableRecord(int id, String name, int capacity, String status) {
            this.id = id;
            this.name = name;
            this.capacity = capacity;
            this.status = status;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getCapacity() { return capacity; }
        public String getStatus() { return status; }
    }
}
