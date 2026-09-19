package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public List<Payment> findAll() throws SQLException {
        String sql =
                "SELECT p.payment_id, p.order_id, p.payment_date, " +
                "p.amount, p.payment_method " +
                "FROM payments p " +
                "ORDER BY p.payment_id DESC";

        List<Payment> payments = new ArrayList<>();

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                payments.add(new Payment(
                        resultSet.getInt("payment_id"),
                        resultSet.getInt("order_id"),
                        resultSet.getTimestamp("payment_date"),
                        resultSet.getDouble("amount"),
                        resultSet.getString("payment_method")
                ));
            }
        }

        return payments;
    }

}
