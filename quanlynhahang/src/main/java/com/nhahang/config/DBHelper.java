package com.nhahang.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {

    private static final String URL =
            "jdbc:mysql://localhost:3306/quanlynhahang"
            + "?useSSL=false"
            + "&serverTimezone=Asia/Ho_Chi_Minh"
            + "&characterEncoding=UTF-8";

    private static final String USER = "root";

    private static final String PASSWORD = "Root#123456";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }

    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            if (connection != null) {
                System.out.println("=================================");
                System.out.println("KET NOI MYSQL THANH CONG!");
                System.out.println("Database: quanlynhahang");
                System.out.println("=================================");
            }

        } catch (SQLException e) {

            System.out.println("KET NOI MYSQL THAT BAI!");
            System.out.println("Loi: " + e.getMessage());
        }
    }
}