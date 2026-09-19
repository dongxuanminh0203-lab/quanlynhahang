package com.nhahang.dao;

import com.nhahang.config.DBHelper;
import com.nhahang.model.Invoice;
import com.nhahang.model.InvoiceDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // Lấy danh sách hóa đơn
    public List<Invoice> findAll() throws SQLException {

        String sql =
                "SELECT o.order_id, o.table_id, rt.table_name, " +
                "o.employee_id, o.status, o.total_amount " +
                "FROM orders o " +
                "LEFT JOIN restaurant_tables rt " +
                "ON rt.table_id = o.table_id " +
                "ORDER BY o.order_id DESC";

        List<Invoice> list = new ArrayList<>();

        try (
                Connection conn = DBHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(new Invoice(
                        rs.getInt("order_id"),
                        rs.getInt("table_id"),
                        rs.getString("table_name"),
                        rs.getInt("employee_id"),
                        rs.getString("status"),
                        rs.getDouble("total_amount")
                ));
            }
        }

        return list;
    }

    // Lấy chi tiết hóa đơn
    public List<InvoiceDetail> findDetails(int orderId)
            throws SQLException {

        String sql =
                "SELECT od.product_id, p.product_name, " +
                "od.quantity, od.unit_price, od.note " +
                "FROM order_details od " +
                "LEFT JOIN products p " +
                "ON p.product_id = od.product_id " +
                "WHERE od.order_id = ? " +
                "ORDER BY od.product_id";

        List<InvoiceDetail> list = new ArrayList<>();

        try (
                Connection conn = DBHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(new InvoiceDetail(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price"),
                            rs.getString("note")
                    ));
                }
            }
        }

        return list;
    }

    // Thanh toán hóa đơn
        public void payInvoice(
            int orderId,
            int tableId,
            double amount,
            String paymentMethod
        )
            throws SQLException {

        String updateOrder =
                "UPDATE orders " +
                "SET status = 'PAID' " +
                "WHERE order_id = ? AND status = 'OPEN'";

        String updateTable =
                "UPDATE restaurant_tables " +
                "SET status = 'EMPTY' " +
                "WHERE table_id = ?";

            String insertPayment =
                "INSERT INTO payments " +
                "(order_id, amount, payment_method) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DBHelper.getConnection()) {

            conn.setAutoCommit(false);

            try {

                int affected;

                try (PreparedStatement ps =
                             conn.prepareStatement(updateOrder)) {

                    ps.setInt(1, orderId);

                    affected = ps.executeUpdate();
                }

                if (affected == 0) {
                    throw new SQLException(
                            "Hóa đơn không tồn tại hoặc đã thanh toán."
                    );
                }

                try (PreparedStatement ps =
                             conn.prepareStatement(insertPayment)) {

                    ps.setInt(1, orderId);
                    ps.setDouble(2, amount);
                    ps.setString(3, paymentMethod);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             conn.prepareStatement(updateTable)) {

                    ps.setInt(1, tableId);
                    ps.executeUpdate();
                }

                conn.commit();

            } catch (SQLException e) {

                conn.rollback();
                throw e;

            } finally {

                conn.setAutoCommit(true);
            }
        }
    }

}