package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO {

    public int countPendingPayments() throws SQLException {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'OPEN'";
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
