package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // Lấy danh sách hóa đơn
    public List<InvoiceRecord> findAll() throws SQLException {

        String sql =
                "SELECT o.order_id, o.table_id, rt.table_name, " +
                "o.employee_id, o.status, o.total_amount " +
                "FROM orders o " +
                "LEFT JOIN restaurant_tables rt " +
                "ON rt.table_id = o.table_id " +
                "ORDER BY o.order_id DESC";

        List<InvoiceRecord> list = new ArrayList<>();

        try (
                Connection conn = DBHelper.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(new InvoiceRecord(
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
    public void payInvoice(int orderId, int tableId)
            throws SQLException {

        String updateOrder =
                "UPDATE orders " +
                "SET status = 'PAID' " +
                "WHERE order_id = ? AND status = 'OPEN'";

        String updateTable =
                "UPDATE restaurant_tables " +
                "SET status = 'EMPTY' " +
                "WHERE table_id = ?";

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

    // =========================
    // HÓA ĐƠN
    // =========================

    public static class InvoiceRecord {

        private final int orderId;
        private final int tableId;
        private final String tableName;
        private final int employeeId;
        private final String status;
        private final double totalAmount;

        public InvoiceRecord(
                int orderId,
                int tableId,
                String tableName,
                int employeeId,
                String status,
                double totalAmount
        ) {

            this.orderId = orderId;
            this.tableId = tableId;
            this.tableName = tableName;
            this.employeeId = employeeId;
            this.status = status;
            this.totalAmount = totalAmount;
        }

        public int getOrderId() {
            return orderId;
        }

        public int getTableId() {
            return tableId;
        }

        public String getTableName() {
            return tableName;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public String getStatus() {
            return status;
        }

        public double getTotalAmount() {
            return totalAmount;
        }
    }

    // =========================
    // CHI TIẾT HÓA ĐƠN
    // =========================

    public static class InvoiceDetail {

        private final int productId;
        private final String productName;
        private final int quantity;
        private final double unitPrice;
        private final String note;

        public InvoiceDetail(
                int productId,
                String productName,
                int quantity,
                double unitPrice,
                String note
        ) {

            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.note = note;
        }

        public int getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public String getNote() {
            return note;
        }

        public double getAmount() {
            return quantity * unitPrice;
        }
    }
}