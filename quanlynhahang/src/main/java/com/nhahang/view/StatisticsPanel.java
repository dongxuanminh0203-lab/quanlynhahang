package com.nhahang.view;

import com.nhahang.dao.DashboardDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatisticsPanel extends JPanel {

    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color GREEN = new Color(22, 163, 74);
    private static final Color ORANGE = new Color(245, 158, 11);
    private static final Color RED = new Color(220, 38, 38);

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final NumberFormat currencyFormatter = NumberFormat.getInstance(new Locale("vi", "VN"));

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(30, 36, 30, 36));
        loadStatistics();
    }

    private void loadStatistics() {
        new SwingWorker<DashboardDAO.DashboardStats, Void>() {
            @Override
            protected DashboardDAO.DashboardStats doInBackground() throws Exception {
                return dashboardDAO.getStatistics();
            }

            @Override
            protected void done() {
                try {
                    DashboardDAO.DashboardStats stats = get();
                    removeAll();
                    add(createContent(stats), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } catch (Exception e) {
                    showError("Không thể tải thống kê:\n" + getErrorMessage(e));
                    removeAll();
                    add(createFallbackPanel(), BorderLayout.CENTER);
                    revalidate();
                    repaint();
                }
            }
        }.execute();
    }

    private JPanel createContent(DashboardDAO.DashboardStats stats) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel title = new JLabel("Thống kê");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Tổng quan hoạt động kinh doanh của nhà hàng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);
        content.add(header);
        content.add(Box.createVerticalStrut(24));

        JPanel statGrid = new JPanel(new GridLayout(1, 4, 18, 0));
        statGrid.setOpaque(false);
        statGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        statGrid.add(createStatCard(formatMoney(stats.getTotalRevenue()), "Doanh thu", BLUE, "money"));
        statGrid.add(createStatCard(String.valueOf(stats.getPaidOrders()), "Đơn đã thanh toán", GREEN, "receipt"));
        statGrid.add(createStatCard(String.valueOf(stats.getOpenOrders()), "Đơn đang mở", ORANGE, "pending"));
        statGrid.add(createStatCard(String.valueOf(stats.getCustomerCount()), "Khách hàng", RED, "user"));

        content.add(statGrid);
        content.add(Box.createVerticalStrut(24));

        JPanel chartPanel = createRevenueChartCard(stats);
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(chartPanel);
        content.add(Box.createVerticalStrut(20));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 20, 0));
        bottom.setOpaque(false);
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        bottom.add(createTableSummaryPanel(stats.getTableSummary()));
        bottom.add(createEmployeeSummaryPanel(stats.getEmployeeSummary()));

        content.add(bottom);
        content.add(Box.createVerticalStrut(20));

        bottom = new JPanel(new GridLayout(1, 2, 20, 0));
        bottom.setOpaque(false);
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        bottom.add(createSalesTable(stats.getTopProducts()));
        bottom.add(createSummaryPanel(stats));

        content.add(bottom);
        return content;
    }

    private JPanel createRevenueChartCard(DashboardDAO.DashboardStats stats) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Doanh thu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        String[] modeOptions = {"Theo ngày", "Theo tháng"};
        JComboBox<String> modeBox = new JComboBox<>(modeOptions);
        modeBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        modeBox.setFocusable(false);

        JPanel chartHolder = new JPanel(new BorderLayout());
        chartHolder.setOpaque(false);

        RevenueChartPanel chart = new RevenueChartPanel(stats.getRevenueByDay());
        chartHolder.add(chart, BorderLayout.CENTER);

        modeBox.addActionListener(e -> {
            List<DashboardDAO.RevenuePoint> selected = "Theo ngày".equals(modeBox.getSelectedItem())
                    ? stats.getRevenueByDay() : stats.getRevenueByMonth();
            chart.setSeries(selected);
            chart.repaint();
        });

        header.add(title, BorderLayout.WEST);
        header.add(modeBox, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);
        card.add(chartHolder, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSummaryPanel(DashboardDAO.DashboardStats stats) {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Tóm tắt hoạt động");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        addSummaryRow(panel, "Tổng số đơn", String.valueOf(stats.getTotalOrders()));
        addSummaryRow(panel, "Đã thanh toán", String.valueOf(stats.getPaidOrders()));
        addSummaryRow(panel, "Đang mở", String.valueOf(stats.getOpenOrders()));
        addSummaryRow(panel, "Khách hàng", String.valueOf(stats.getCustomerCount()));

        return panel;
    }

    private void addSummaryRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel left = new JLabel(label);
        left.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        left.setForeground(TEXT_GRAY);

        JLabel right = new JLabel(value);
        right.setFont(new Font("Segoe UI", Font.BOLD, 15));
        right.setForeground(TEXT);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        parent.add(row);
    }

    private JPanel createTableSummaryPanel(List<DashboardDAO.TableSummary> items) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Thống kê theo bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Bàn", "Trạng thái", "Số lượng"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (DashboardDAO.TableSummary item : items) {
            model.addRow(new Object[]{
                    item.getName(),
                    item.getStatus(),
                    item.getCount()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.add(title, BorderLayout.NORTH);
        inner.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmployeeSummaryPanel(List<DashboardDAO.EmployeeSummary> items) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Thống kê theo nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Nhân viên", "Số đơn", "Doanh thu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (DashboardDAO.EmployeeSummary item : items) {
            model.addRow(new Object[]{
                    item.getName(),
                    item.getOrderCount(),
                    formatMoney(item.getRevenue())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.add(title, BorderLayout.NORTH);
        inner.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSalesTable(List<DashboardDAO.TopProduct> items) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel title = new JLabel("Sản phẩm bán chạy");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Tên món", "SL bán", "Doanh thu"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (DashboardDAO.TopProduct item : items) {
            model.addRow(new Object[]{
                    item.getName(),
                    item.getQuantity(),
                    formatMoney(item.getRevenue())
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.add(title, BorderLayout.NORTH);
        inner.add(new JScrollPane(table), BorderLayout.CENTER);

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String value, String label, Color color, String icon) {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(iconSymbol(icon));
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        iconLabel.setForeground(color);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(42, 42));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        valueLabel.setForeground(TEXT);

        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelText.setForeground(TEXT_GRAY);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BorderLayout(8, 0));
        left.add(valueLabel, BorderLayout.NORTH);
        left.add(labelText, BorderLayout.SOUTH);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(left, BorderLayout.CENTER);

        return card;
    }

    private String iconSymbol(String icon) {
        switch (icon) {
            case "money": return "₫";
            case "receipt": return "✓";
            case "pending": return "◔";
            case "user": return "👤";
            default: return "•";
        }
    }

    private JPanel createFallbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND);
        JLabel label = new JLabel("Không có dữ liệu thống kê để hiển thị.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(TEXT_GRAY);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private String formatMoney(double value) {
        return currencyFormatter.format(value) + " ₫";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private String getErrorMessage(Exception e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() != null ? cause.getMessage() : cause.toString();
    }

    private static class RevenueChartPanel extends JPanel {
        private List<DashboardDAO.RevenuePoint> series = new ArrayList<>();

        public RevenueChartPanel(List<DashboardDAO.RevenuePoint> series) {
            this.series = series == null ? new ArrayList<>() : series;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 220));
        }

        public void setSeries(List<DashboardDAO.RevenuePoint> series) {
            this.series = series == null ? new ArrayList<>() : series;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int paddingLeft = 42;
            int paddingRight = 20;
            int paddingTop = 20;
            int paddingBottom = 30;

            int chartWidth = getWidth() - paddingLeft - paddingRight;
            int chartHeight = getHeight() - paddingTop - paddingBottom;

            if (series.isEmpty()) {
                g2.setColor(new Color(107, 118, 135));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString("Chưa có dữ liệu", paddingLeft + 20, getHeight() / 2);
                g2.dispose();
                return;
            }

            double maxValue = 0;
            for (DashboardDAO.RevenuePoint point : series) {
                if (point.getRevenue() > maxValue) {
                    maxValue = point.getRevenue();
                }
            }

            if (maxValue <= 0) {
                maxValue = 1;
            }

            g2.setColor(new Color(225, 229, 236));
            g2.drawLine(paddingLeft, paddingTop, paddingLeft, getHeight() - paddingBottom);
            g2.drawLine(paddingLeft, getHeight() - paddingBottom, getWidth() - paddingRight, getHeight() - paddingBottom);

            int barWidth = Math.max(18, chartWidth / Math.max(series.size(), 1) - 12);
            int xStart = paddingLeft + 12;

            for (int i = 0; i < series.size(); i++) {
                DashboardDAO.RevenuePoint point = series.get(i);
                int barHeight = (int) ((point.getRevenue() / maxValue) * (chartHeight - 10));
                int x = xStart + i * (barWidth + 10);
                int y = getHeight() - paddingBottom - barHeight;

                g2.setColor(new Color(37, 99, 235));
                g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

                g2.setColor(new Color(107, 118, 135));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.drawString(point.getLabel(), x, getHeight() - 10);
            }

            g2.dispose();
        }
    }
}

