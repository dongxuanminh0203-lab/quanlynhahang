package com.nhahang.dao;

import com.nhahang.config.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
        return getStatistics(null, null);
    }

    public DashboardStats getStatistics(LocalDate fromDate, LocalDate toDate) throws SQLException {
        DashboardStats stats = new DashboardStats();
        boolean filterByDate = (fromDate != null || toDate != null) && hasColumn("orders", "order_date");
        String dateCondition = filterByDate ? buildDateCondition("order_date", fromDate, toDate) : "";
        stats.totalRevenue = getDouble("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'PAID'" + dateCondition, fromDate, toDate, filterByDate);
        stats.totalOrders = getInt("SELECT COUNT(*) FROM orders WHERE 1 = 1" + dateCondition, fromDate, toDate, filterByDate);
        stats.paidOrders = getInt("SELECT COUNT(*) FROM orders WHERE status = 'PAID'" + dateCondition, fromDate, toDate, filterByDate);
        stats.openOrders = getInt("SELECT COUNT(*) FROM orders WHERE status = 'OPEN'" + dateCondition, fromDate, toDate, filterByDate);
        stats.customerCount = countTableRows("customers");
        stats.topProducts = getTopProducts(fromDate, toDate, filterByDate);
        stats.tableSummary = getTableSummary();
        stats.employeeSummary = getEmployeeSummary(fromDate, toDate, filterByDate);
        return stats;
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

    private List<EmployeeSummary> getEmployeeSummary(LocalDate fromDate, LocalDate toDate, boolean filterByDate) throws SQLException {
        List<EmployeeSummary> list = new ArrayList<>();

        if (!tableExists("employees") || !tableExists("orders")) {
            return list;
        }

        String dateCondition = filterByDate ? buildDateCondition("o.order_date", fromDate, toDate) : "";
        String sql = "SELECT e.employee_name, COUNT(o.order_id) AS order_count, "
                + "COALESCE(SUM(o.total_amount), 0) AS revenue "
            + "FROM employees e LEFT JOIN orders o ON o.employee_id = e.employee_id" + dateCondition + " "
                + "GROUP BY e.employee_id, e.employee_name ORDER BY revenue DESC, order_count DESC LIMIT 5";

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setDateParameters(statement, fromDate, toDate, filterByDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(new EmployeeSummary(
                            resultSet.getString("employee_name"),
                            resultSet.getInt("order_count"),
                            resultSet.getDouble("revenue")
                    ));
                }
            }
        }
        return list;
    }

    private List<TopProduct> getTopProducts(LocalDate fromDate, LocalDate toDate, boolean filterByDate) throws SQLException {
        List<TopProduct> list = new ArrayList<>();
        if (!tableExists("products") || !tableExists("order_details")) {
            return list;
        }

        String dateCondition = filterByDate ? buildDateCondition("o.order_date", fromDate, toDate) : "";
        String sql = "SELECT p.product_name, SUM(od.quantity) AS sold_qty, "
                + "SUM(od.quantity * od.unit_price) AS revenue "
                + "FROM order_details od "
            + "LEFT JOIN orders o ON o.order_id = od.order_id "
                + "LEFT JOIN products p ON p.product_id = od.product_id "
            + (dateCondition.isEmpty() ? "" : "WHERE " + dateCondition.substring(5) + " ")
                + "GROUP BY p.product_id, p.product_name "
                + "ORDER BY sold_qty DESC, revenue DESC LIMIT 5";

        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setDateParameters(statement, fromDate, toDate, filterByDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(new TopProduct(
                            resultSet.getString("product_name"),
                            resultSet.getInt("sold_qty"),
                            resultSet.getDouble("revenue")
                    ));
                }
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

    private int getInt(String sql, LocalDate fromDate, LocalDate toDate, boolean filterByDate) throws SQLException {
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setDateParameters(statement, fromDate, toDate, filterByDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }
                return resultSet.getInt(1);
            }
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

    private double getDouble(String sql, LocalDate fromDate, LocalDate toDate, boolean filterByDate) throws SQLException {
        try (Connection connection = DBHelper.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setDateParameters(statement, fromDate, toDate, filterByDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }
                return resultSet.getDouble(1);
            }
        }
    }

    private String buildDateCondition(String column, LocalDate fromDate, LocalDate toDate) {
        StringBuilder condition = new StringBuilder();
        if (fromDate != null) {
            condition.append(" AND ").append(column).append(" >= ?");
        }
        if (toDate != null) {
            condition.append(" AND ").append(column).append(" < DATE_ADD(?, INTERVAL 1 DAY)");
        }
        return condition.toString();
    }

    private void setDateParameters(PreparedStatement statement, LocalDate fromDate, LocalDate toDate, boolean filterByDate) throws SQLException {
        if (!filterByDate) {
            return;
        }
        int index = 1;
        if (fromDate != null) {
            statement.setDate(index++, java.sql.Date.valueOf(fromDate));
        }
        if (toDate != null) {
            statement.setDate(index, java.sql.Date.valueOf(toDate));
        }
    }

    public static class DashboardStats {
        private double totalRevenue;
        private int totalOrders;
        private int paidOrders;
        private int openOrders;
        private int customerCount;
        private List<TopProduct> topProducts = new ArrayList<>();
        private List<TableSummary> tableSummary = new ArrayList<>();
        private List<EmployeeSummary> employeeSummary = new ArrayList<>();

        public double getTotalRevenue() { return totalRevenue; }
        public int getTotalOrders() { return totalOrders; }
        public int getPaidOrders() { return paidOrders; }
        public int getOpenOrders() { return openOrders; }
        public int getCustomerCount() { return customerCount; }
        public List<TopProduct> getTopProducts() { return topProducts; }
        public List<TableSummary> getTableSummary() { return tableSummary; }
        public List<EmployeeSummary> getEmployeeSummary() { return employeeSummary; }
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
