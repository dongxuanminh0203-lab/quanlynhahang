package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public DashboardStats getStatistics() throws SQLException {
        DashboardStats stats = new DashboardStats();
        stats.totalRevenue = getDouble("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'PAID'");
        stats.totalOrders = getInt("SELECT COUNT(*) FROM orders");
        stats.paidOrders = getInt("SELECT COUNT(*) FROM orders WHERE status = 'PAID'");
        stats.openOrders = getInt("SELECT COUNT(*) FROM orders WHERE status = 'OPEN'");
        stats.customerCount = countTableRows("customers");
        stats.revenueByDay = getRevenueByDay();
        stats.revenueByMonth = getRevenueByMonth();
        stats.topProducts = getTopProducts();
        stats.tableSummary = getTableSummary();
        stats.employeeSummary = getEmployeeSummary();
        return stats;
    }

    private List<RevenuePoint> getRevenueByDay() throws SQLException {
        String sql = "SELECT DATE_FORMAT(created_at, '%d/%m') AS label, "
                + "COALESCE(SUM(total_amount), 0) AS revenue "
                + "FROM orders WHERE status = 'PAID' AND created_at IS NOT NULL "
                + "GROUP BY DATE(created_at) ORDER BY created_at DESC LIMIT 7";

        if (!hasColumn("orders", "created_at")) {
            return new ArrayList<>();
        }

        List<RevenuePoint> list = new ArrayList<>();
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(new RevenuePoint(
                        resultSet.getString("label"),
                        resultSet.getDouble("revenue")
                ));
            }
        }

        reverse(list);
        return list;
    }

    private List<RevenuePoint> getRevenueByMonth() throws SQLException {
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS label, "
                + "COALESCE(SUM(total_amount), 0) AS revenue "
                + "FROM orders WHERE status = 'PAID' AND created_at IS NOT NULL "
                + "GROUP BY DATE_FORMAT(created_at, '%Y-%m') "
                + "ORDER BY created_at ASC LIMIT 6";

        if (!hasColumn("orders", "created_at")) {
            return new ArrayList<>();
        }

        List<RevenuePoint> list = new ArrayList<>();
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(new RevenuePoint(
                        resultSet.getString("label"),
                        resultSet.getDouble("revenue")
                ));
            }
        }
        return list;
    }

    private List<TableSummary> getTableSummary() throws SQLException {
        List<TableSummary> list = new ArrayList<>();
        String sql = "SELECT table_name, status, COUNT(*) AS table_count "
                + "FROM restaurant_tables GROUP BY table_name, status ORDER BY table_name";

        if (!tableExists("restaurant_tables")) {
            return list;
        }

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(new TableSummary(
                        resultSet.getString("table_name"),
                        resultSet.getString("status"),
                        resultSet.getInt("table_count")
                ));
            }
        }
        return list;
    }

    private List<EmployeeSummary> getEmployeeSummary() throws SQLException {
        List<EmployeeSummary> list = new ArrayList<>();

        if (!tableExists("employees") || !tableExists("orders")) {
            return list;
        }

        String sql = "SELECT e.employee_name, COUNT(o.order_id) AS order_count, "
                + "COALESCE(SUM(o.total_amount), 0) AS revenue "
                + "FROM employees e LEFT JOIN orders o ON o.employee_id = e.employee_id "
                + "GROUP BY e.employee_id, e.employee_name ORDER BY revenue DESC, order_count DESC LIMIT 5";

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(new EmployeeSummary(
                        resultSet.getString("employee_name"),
                        resultSet.getInt("order_count"),
                        resultSet.getDouble("revenue")
                ));
            }
        }
        return list;
    }

    private List<TopProduct> getTopProducts() throws SQLException {
        List<TopProduct> list = new ArrayList<>();
        if (!tableExists("products") || !tableExists("order_details")) {
            return list;
        }

        String sql = "SELECT p.product_name, SUM(od.quantity) AS sold_qty, "
                + "SUM(od.quantity * od.unit_price) AS revenue "
                + "FROM order_details od "
                + "LEFT JOIN products p ON p.product_id = od.product_id "
                + "GROUP BY p.product_id, p.product_name "
                + "ORDER BY sold_qty DESC, revenue DESC LIMIT 5";

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(new TopProduct(
                        resultSet.getString("product_name"),
                        resultSet.getInt("sold_qty"),
                        resultSet.getDouble("revenue")
                ));
            }
        }
        return list;
    }

    private int countTableRows(String tableName) throws SQLException {
        if (!tableExists(tableName)) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.tables "
                + "WHERE table_schema = DATABASE() AND table_name = ?";

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    private boolean hasColumn(String tableName, String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM information_schema.columns "
                + "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tableName);
            statement.setString(2, columnName);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    private void reverse(List<RevenuePoint> list) {
        int left = 0;
        int right = list.size() - 1;
        while (left < right) {
            RevenuePoint temp = list.get(left);
            list.set(left, list.get(right));
            list.set(right, temp);
            left++;
            right--;
        }
    }

    private int getInt(String sql) throws SQLException {
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return 0;
            }
            return resultSet.getInt(1);
        }
    }

    private double getDouble(String sql) throws SQLException {
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return 0;
            }
            return resultSet.getDouble(1);
        }
    }

    public static class DashboardStats {
        private double totalRevenue;
        private int totalOrders;
        private int paidOrders;
        private int openOrders;
        private int customerCount;
        private List<RevenuePoint> revenueByDay = new ArrayList<>();
        private List<RevenuePoint> revenueByMonth = new ArrayList<>();
        private List<TopProduct> topProducts = new ArrayList<>();
        private List<TableSummary> tableSummary = new ArrayList<>();
        private List<EmployeeSummary> employeeSummary = new ArrayList<>();

        public double getTotalRevenue() { return totalRevenue; }
        public int getTotalOrders() { return totalOrders; }
        public int getPaidOrders() { return paidOrders; }
        public int getOpenOrders() { return openOrders; }
        public int getCustomerCount() { return customerCount; }
        public List<RevenuePoint> getRevenueByDay() { return revenueByDay; }
        public List<RevenuePoint> getRevenueByMonth() { return revenueByMonth; }
        public List<TopProduct> getTopProducts() { return topProducts; }
        public List<TableSummary> getTableSummary() { return tableSummary; }
        public List<EmployeeSummary> getEmployeeSummary() { return employeeSummary; }
    }

    public static class RevenuePoint {
        private final String label;
        private final double revenue;

        public RevenuePoint(String label, double revenue) {
            this.label = label;
            this.revenue = revenue;
        }

        public String getLabel() { return label; }
        public double getRevenue() { return revenue; }
    }

    public static class TopProduct {
        private final String name;
        private final int quantity;
        private final double revenue;

        public TopProduct(String name, int quantity, double revenue) {
            this.name = name;
            this.quantity = quantity;
            this.revenue = revenue;
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getRevenue() { return revenue; }
    }

    public static class TableSummary {
        private final String name;
        private final String status;
        private final int count;

        public TableSummary(String name, String status, int count) {
            this.name = name;
            this.status = status;
            this.count = count;
        }

        public String getName() { return name; }
        public String getStatus() { return status; }
        public int getCount() { return count; }
    }

    public static class EmployeeSummary {
        private final String name;
        private final int orderCount;
        private final double revenue;

        public EmployeeSummary(String name, int orderCount, double revenue) {
            this.name = name;
            this.orderCount = orderCount;
            this.revenue = revenue;
        }

        public String getName() { return name; }
        public int getOrderCount() { return orderCount; }
        public double getRevenue() { return revenue; }
    }
}
