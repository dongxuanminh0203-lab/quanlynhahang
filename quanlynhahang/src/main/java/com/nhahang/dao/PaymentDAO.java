package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public List<PaymentRecord> findAll() throws SQLException {
        String sql =
                "SELECT p.payment_id, p.order_id, p.payment_date, " +
                "p.amount, p.payment_method " +
                "FROM payments p " +
                "ORDER BY p.payment_id DESC";

        List<PaymentRecord> payments = new ArrayList<>();

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                payments.add(new PaymentRecord(
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

    public static class PaymentRecord {
        private final int paymentId;
        private final int orderId;
        private final Timestamp paymentDate;
        private final double amount;
        private final String paymentMethod;

        public PaymentRecord(
                int paymentId,
                int orderId,
                Timestamp paymentDate,
                double amount,
                String paymentMethod
        ) {
            this.paymentId = paymentId;
            this.orderId = orderId;
            this.paymentDate = paymentDate;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
        }

        public int getPaymentId() {
            return paymentId;
        }

        public int getOrderId() {
            return orderId;
        }

        public Timestamp getPaymentDate() {
            return paymentDate;
        }

        public double getAmount() {
            return amount;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }
    }
}
