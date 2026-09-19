package com.nhahang.view;

import com.nhahang.dao.DashboardDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
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
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(30, 36, 30, 36));
        loadStatistics();
    }

    private void loadStatistics() {
        loadStatistics(null, null);
    }

    private void loadStatistics(LocalDate fromDate, LocalDate toDate) {
        new SwingWorker<DashboardDAO.DashboardStats, Void>() {
            @Override
            protected DashboardDAO.DashboardStats doInBackground() throws Exception {
                return dashboardDAO.getStatistics(fromDate, toDate);
            }

            @Override
            protected void done() {
                try {
                    DashboardDAO.DashboardStats stats = get();
                    removeAll();
                    add(createContent(stats, fromDate, toDate), BorderLayout.CENTER);
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

    private JPanel createContent(DashboardDAO.DashboardStats stats, LocalDate fromDate, LocalDate toDate) {
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
        header.add(createFilterPanel(fromDate, toDate), BorderLayout.EAST);
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

    private JPanel createFilterPanel(LocalDate fromDate, LocalDate toDate) {
        JTextField fromField = createDateField();
        JTextField toField = createDateField();
        JButton filterButton = new JButton("Lọc");
        JButton clearButton = new JButton("Xóa");

        fromField.setToolTipText("Nhập ngày bắt đầu, ví dụ 02092026");
        toField.setToolTipText("Nhập ngày kết thúc, ví dụ 03092026");
        if (fromDate != null) {
            fromField.setText(fromDate.format(dateFormatter));
        }
        if (toDate != null) {
            toField.setText(toDate.format(dateFormatter));
        }
        filterButton.setFocusable(false);
        clearButton.setFocusable(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Từ ngày"));
        filterPanel.add(fromField);
        filterPanel.add(new JLabel("Đến ngày"));
        filterPanel.add(toField);
        filterPanel.add(filterButton);
        filterPanel.add(clearButton);

        filterButton.addActionListener(e -> applyFilter(fromField, toField));
        clearButton.addActionListener(e -> {
            fromField.setText("");
            toField.setText("");
            loadStatistics();
        });

        return filterPanel;
    }

    private JTextField createDateField() {
        JTextField field = new JTextField(10);
        ((javax.swing.text.AbstractDocument) field.getDocument()).setDocumentFilter(new DateDocumentFilter());
        field.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent event) {
                new DatePickerPopup(field).show(field, 0, field.getHeight());
            }
        });
        return field;
    }

    private void applyFilter(JTextField fromField, JTextField toField) {
        try {
            LocalDate fromDate = parseDate(fromField.getText());
            LocalDate toDate = parseDate(toField.getText());
            if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
            }
            loadStatistics(fromDate, toDate);
        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage() == null
                    ? "Ngày lọc không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy."
                    : exception.getMessage());
        }
    }

    private LocalDate parseDate(String value) {
        String text = value == null ? "" : value.trim();
        return text.isEmpty() ? null : LocalDate.parse(text, dateFormatter);
    }

    private static class DateDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass bypass, int offset, String text, AttributeSet attributes)
                throws BadLocationException {
            replace(bypass, offset, 0, text, attributes);
        }

        @Override
        public void remove(FilterBypass bypass, int offset, int length) throws BadLocationException {
            replace(bypass, offset, length, "", null);
        }

        @Override
        public void replace(FilterBypass bypass, int offset, int length, String text, AttributeSet attributes)
                throws BadLocationException {
            String current = bypass.getDocument().getText(0, bypass.getDocument().getLength());
            String next = current.substring(0, offset) + (text == null ? "" : text)
                    + current.substring(offset + length);
            String digits = next.replaceAll("\\D", "");
            if (digits.length() > 8) {
                digits = digits.substring(0, 8);
            }

            StringBuilder formatted = new StringBuilder(digits);
            if (digits.length() > 4) {
                formatted.insert(4, '/');
            }
            if (digits.length() > 2) {
                formatted.insert(2, '/');
            }

            bypass.replace(0, bypass.getDocument().getLength(), formatted.toString(), attributes);
        }
    }

    private class DatePickerPopup extends JPopupMenu {
        private final JTextField field;
        private YearMonth month;

        DatePickerPopup(JTextField field) {
            this.field = field;
            this.month = readDate().map(YearMonth::from).orElse(YearMonth.now());
            rebuild();
        }

        private java.util.Optional<LocalDate> readDate() {
            try {
                return java.util.Optional.ofNullable(parseDate(field.getText()));
            } catch (IllegalArgumentException exception) {
                return java.util.Optional.empty();
            }
        }

        private void rebuild() {
            removeAll();
            setBorder(BorderFactory.createLineBorder(BORDER));
            setBackground(WHITE);

            JPanel header = new JPanel(new BorderLayout(8, 0));
            header.setBackground(WHITE);
            JButton previous = new JButton("<");
            JButton next = new JButton(">");
            JLabel monthLabel = new JLabel(month.format(DateTimeFormatter.ofPattern("MM/yyyy")), SwingConstants.CENTER);
            monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            previous.setFocusable(false);
            next.setFocusable(false);
            previous.addActionListener(event -> {
                month = month.minusMonths(1);
                rebuild();
                show(field, 0, field.getHeight());
            });
            next.addActionListener(event -> {
                month = month.plusMonths(1);
                rebuild();
                show(field, 0, field.getHeight());
            });
            header.add(previous, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(next, BorderLayout.EAST);
            add(header);

            JPanel calendar = new JPanel(new GridLayout(0, 7, 2, 2));
            calendar.setBorder(new EmptyBorder(6, 6, 6, 6));
            calendar.setBackground(WHITE);
            for (String day : new String[]{"T2", "T3", "T4", "T5", "T6", "T7", "CN"}) {
                JLabel label = new JLabel(day, SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setForeground(TEXT_GRAY);
                calendar.add(label);
            }

            int firstDayOffset = month.atDay(1).getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
            for (int index = 0; index < firstDayOffset; index++) {
                calendar.add(new JLabel());
            }
            LocalDate selectedDate = readDate().orElse(null);
            for (int day = 1; day <= month.lengthOfMonth(); day++) {
                LocalDate date = month.atDay(day);
                JButton dayButton = new JButton(String.valueOf(day));
                dayButton.setFocusable(false);
                dayButton.setMargin(new Insets(2, 5, 2, 5));
                if (date.equals(selectedDate)) {
                    dayButton.setBackground(new Color(219, 234, 254));
                }
                dayButton.addActionListener(event -> {
                    field.setText(date.format(dateFormatter));
                    setVisible(false);
                });
                calendar.add(dayButton);
            }
            add(calendar);
            pack();
        }
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

}

