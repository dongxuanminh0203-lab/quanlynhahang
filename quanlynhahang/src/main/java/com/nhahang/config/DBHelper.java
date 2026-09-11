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

    private static final String PASSWORD = "123456";

    private DBHelper() {
    }

    public static Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}
