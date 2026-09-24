package com.nhahang.view;

import com.nhahang.controller.KitchenController;
import com.nhahang.model.KitchenOrderItem;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class KitchenPanel extends JPanel {
    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color PRIMARY = new Color(37, 99, 235);

    private final KitchenController controller = new KitchenController();
    private final List<KitchenOrderItem> items = new ArrayList<>();
    private final Timer refreshTimer;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel summary;

    public KitchenPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        buildUI();
        loadItems();
        refreshTimer = new Timer(10000, event -> loadItems());
        refreshTimer.start();
    }

    @Override
    public void removeNotify() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.removeNotify();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Nhà bếp");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Theo dõi món mới gọi và chuẩn bị món ăn");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        titleBox.add(title);
        titleBox.add(subtitle);
        JButton refresh = new JButton("Làm mới");
        refresh.addActionListener(e -> loadItems());
        heading.add(titleBox, BorderLayout.WEST);
        heading.add(refresh, BorderLayout.EAST);
        main.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        tableModel = new DefaultTableModel(
                new Object[]{"Đơn", "Bàn", "Món ăn", "SL", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(280);
        table.getColumnModel().getColumn(3).setPreferredWidth(55);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2) showDetails();
            }
        });
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(10, 0));
        actions.setOpaque(false);
        summary = new JLabel("Chưa có món");
        summary.setFont(new Font("Segoe UI", Font.BOLD, 14));
        summary.setForeground(PRIMARY);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton details = new JButton("Xem thành phần");
        details.addActionListener(e -> showDetails());
        JComboBox<String> status = new JComboBox<>(new String[]{"PENDING", "COOKING", "READY"});
        JButton update = new JButton("Cập nhật trạng thái");
        update.setBackground(PRIMARY);
        update.setForeground(Color.WHITE);
        update.setFocusPainted(false);
        update.addActionListener(e -> updateStatus(String.valueOf(status.getSelectedItem())));
        right.add(details);
        right.add(status);
        right.add(update);
        actions.add(summary, BorderLayout.WEST);
        actions.add(right, BorderLayout.EAST);
        content.add(actions, BorderLayout.SOUTH);
        main.add(content, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private void loadItems() {
        summary.setText("Đang tải danh sách món...");
        new SwingWorker<List<KitchenOrderItem>, Void>() {
            @Override
            protected List<KitchenOrderItem> doInBackground() throws Exception {
                return controller.loadItems();
            }

            @Override
            protected void done() {
                try {
                    items.clear();
                    items.addAll(get());
                    refreshTable();
                } catch (Exception exception) {
                    summary.setText("Không thể tải món bếp: " + rootMessage(exception));
                }
            }
        }.execute();
    }

    private void refreshTable() {
        KitchenOrderItem selected = selectedItemWithoutMessage();
        tableModel.setRowCount(0);
        int pending = 0;
        int selectedRow = -1;
        int rowIndex = 0;
        for (KitchenOrderItem item : items) {
            tableModel.addRow(new Object[]{
                    "#" + item.getOrderId(), item.getTableName(), item.getProductName(),
                    item.getQuantity(), displayStatus(item.getCookingStatus())
            });
            if (selected != null
                    && selected.getOrderId() == item.getOrderId()
                    && selected.getProductId() == item.getProductId()) {
                selectedRow = rowIndex;
            }
            if (!"READY".equals(item.getCookingStatus())) pending++;
            rowIndex++;
        }
        if (selectedRow >= 0) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
            table.scrollRectToVisible(table.getCellRect(selectedRow, 0, true));
        }
        summary.setText(pending + " món cần xử lý | " + items.size() + " món trong danh sách");
    }

    private void showDetails() {
        KitchenOrderItem item = selectedItem();
        if (item == null) return;
        String ingredients = item.getIngredients().trim().isEmpty()
                ? "Chưa cập nhật" : item.getIngredients();
        String note = item.getNote().trim().isEmpty() ? "Không có" : item.getNote();
        JOptionPane.showMessageDialog(this,
                "Món: " + item.getProductName() + "\n"
                        + "Số lượng: " + item.getQuantity() + "\n"
                        + "Thành phần: " + ingredients + "\n"
                        + "Ghi chú: " + note,
                "Chi tiết món ăn - Đơn #" + item.getOrderId(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStatus(String status) {
        KitchenOrderItem item = selectedItem();
        if (item == null) return;
        try {
            controller.updateStatus(item.getOrderId(), item.getProductId(), status);
            item.setCookingStatus(status);
            refreshTable();
        } catch (Exception exception) {
            showError("Không thể cập nhật trạng thái:\n" + exception.getMessage());
        }
    }

    private KitchenOrderItem selectedItem() {
        KitchenOrderItem item = selectedItemWithoutMessage();
        if (item == null) {
            showError("Vui lòng chọn một món trong danh sách.");
        }
        return item;
    }

    private KitchenOrderItem selectedItemWithoutMessage() {
        int row = table == null ? -1 : table.getSelectedRow();
        if (row < 0 || row >= items.size()) {
            return null;
        }
        return items.get(row);
    }

    private String displayStatus(String status) {
        if ("COOKING".equals(status)) return "ĐANG NẤU";
        if ("READY".equals(status)) return "ĐÃ XONG";
        return "CHỜ NẤU";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private String rootMessage(Exception exception) {
        Throwable cause = exception;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }
}
