package com.nhahang.config;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {

    private static final String DATABASE_NAME = "quanlynhahang";
    private static final String DEFAULT_ADMIN_USERNAME = "root";
    private static final String DEFAULT_ADMIN_PASSWORD = "123456";

    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306/?"
                    + "useSSL=false"
                    + "&serverTimezone=Asia/Ho_Chi_Minh"
                    + "&characterEncoding=UTF-8";

    private static final String URL =
            "jdbc:mysql://localhost:3306/" + DATABASE_NAME
                    + "?useSSL=false"
                    + "&serverTimezone=Asia/Ho_Chi_Minh"
                    + "&characterEncoding=UTF-8";

    private static final String USER = "root";

    private static final String PASSWORD = "huy11062005";

    private DBHelper() {
    }

    public static Connection getConnection() throws SQLException {
        createDatabaseIfMissing();
        initializeSchema();
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void createDatabaseIfMissing() throws SQLException {
        try (Connection connection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME);
        }
    }

    private static void initializeSchema() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS users ("
                            + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                            + "username VARCHAR(100) NOT NULL UNIQUE, "
                            + "password VARCHAR(255) NOT NULL, "
                            + "role VARCHAR(20) NOT NULL DEFAULT 'STAFF', "
                            + "employee_id INT NULL, "
                            + "status BOOLEAN NOT NULL DEFAULT TRUE, "
                            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                            + ")"
            );

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS audit_logs ("
                            + "audit_id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                            + "action_name VARCHAR(50) NOT NULL, "
                            + "entity_name VARCHAR(80) NOT NULL, "
                            + "entity_id INT NULL, "
                            + "actor_username VARCHAR(100) NULL, "
                            + "details TEXT NULL, "
                            + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                            + ")"
            );

            addColumnIfMissing(connection, "users", "employee_id", "INT NULL");
            addColumnIfMissing(connection, "users", "status", "BOOLEAN NOT NULL DEFAULT TRUE");
            addColumnIfMissing(connection, "users", "created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

            migrateLegacyPasswords();
            ensureDefaultAdmin();
        }
    }

    private static void addColumnIfMissing(Connection connection, String tableName, String columnName, String columnDefinition) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setString(1, tableName);
            check.setString(2, columnName);
            try (ResultSet rs = check.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition;
                    try (Statement stmt = connection.createStatement()) {
                        stmt.executeUpdate(sql);
                    }
                }
            }
        }
    }

    private static void ensureDefaultAdmin() throws SQLException {
        String checkSql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setString(1, DEFAULT_ADMIN_USERNAME);
            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next()) {
                    String insertSql = "INSERT INTO users (username, password, role, status) VALUES (?, ?, 'ADMIN', TRUE)";
                    try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                        insert.setString(1, DEFAULT_ADMIN_USERNAME);
                        insert.setString(2, BCrypt.hashpw(DEFAULT_ADMIN_PASSWORD, BCrypt.gensalt(12)));
                        insert.executeUpdate();
                    }
                }
            }
        }
    }

    private static void migrateLegacyPasswords() throws SQLException {
        String selectSql = "SELECT user_id, password FROM users WHERE password IS NOT NULL";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement select = connection.prepareStatement(selectSql);
             ResultSet rs = select.executeQuery()) {
            while (rs.next()) {
                String stored = rs.getString("password");
                if (stored == null || stored.isBlank()) {
                    continue;
                }
                if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
                    continue;
                }

                String hashed = BCrypt.hashpw(stored, BCrypt.gensalt(12));
                String updateSql = "UPDATE users SET password = ? WHERE user_id = ?";
                try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                    update.setString(1, hashed);
                    update.setInt(2, rs.getInt("user_id"));
                    update.executeUpdate();
                }
            }
        }
    }
}
