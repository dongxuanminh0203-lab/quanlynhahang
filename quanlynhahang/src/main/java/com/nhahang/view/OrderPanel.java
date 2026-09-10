package com.nhahang.view;

import com.nhahang.controller.OrderController;
import com.nhahang.dao.OrderDAO;
import com.nhahang.dao.ProductDAO;
import com.nhahang.dao.TableDAO;
import com.nhahang.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderPanel extends JPanel {
    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color PRIMARY = new Color(37, 99, 235);

    private final User currentUser;
    private final OrderController controller = new OrderController();
    private final List<CartItem> cart = new ArrayList<>();
    private final NumberFormat currency = NumberFormat.getInstance(new Locale("vi", "VN"));
    private JComboBox<TableChoice> tableCombo;
    private JComboBox<ProductChoice> productCombo;
    private JSpinner quantitySpinner;
    private DefaultTableModel cartModel;
    private JLabel totalLabel;

    public OrderPanel(User user) {
        currentUser = user;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        buildUI();
        loadData();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(28, 32, 28, 32));
        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Gọi món");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Chọn bàn và thêm món vào đơn hàng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(5));
        titleBox.add(subtitle);
        main.add(titleBox, BorderLayout.NORTH);
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setOpaque(false);
        content.add(createMenuPanel(), BorderLayout.CENTER);
        content.add(createCartPanel(), BorderLayout.EAST);
        main.add(content, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private JPanel createMenuPanel() {
        JPanel panel = whitePanel();
        panel.setLayout(new BorderLayout(0, 18));
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));
        panel.add(sectionLabel("Chọn món ăn"), BorderLayout.NORTH);
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(fieldLabel("Bàn ăn"));
        tableCombo = new JComboBox<>();
        tableCombo.setPreferredSize(new Dimension(360, 42));
        tableCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(tableCombo);
        form.add(Box.createVerticalStrut(16));
        form.add(fieldLabel("Món ăn"));
        productCombo = new JComboBox<>();
        productCombo.setPreferredSize(new Dimension(360, 42));
        productCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        form.add(productCombo);
        form.add(Box.createVerticalStrut(16));
        form.add(fieldLabel("Số lượng"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantitySpinner.setPreferredSize(new Dimension(120, 42));
        quantitySpinner.setMaximumSize(new Dimension(120, 42));
        form.add(quantitySpinner);
        form.add(Box.createVerticalStrut(22));
        JButton add = new JButton("Thêm vào đơn");
        add.setBackground(PRIMARY);
        add.setForeground(Color.WHITE);
        add.setFocusPainted(false);
        add.setAlignmentX(Component.LEFT_ALIGNMENT);
        add.addActionListener(e -> addProduct());
        form.add(add);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = whitePanel();
        panel.setPreferredSize(new Dimension(560, 0));
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));
        panel.add(sectionLabel("Món trong đơn"), BorderLayout.NORTH);
        cartModel = new DefaultTableModel(new Object[]{"Món ăn", "SL", "Đơn giá", "Thành tiền"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(cartModel);
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        totalLabel = new JLabel("Tổng: 0 đ");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(PRIMARY);
        JButton remove = new JButton("Xóa món chọn");
        remove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) { cart.remove(row); refreshCart(); }
        });
        JButton save = new JButton("Lưu đơn hàng");
        save.setBackground(PRIMARY);
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.addActionListener(e -> saveOrder());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(remove);
        actions.add(save);
        bottom.add(totalLabel, BorderLayout.WEST);
        bottom.add(actions, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadData() {
        try {
            for (TableDAO.TableRecord record : controller.loadTables()) {
                tableCombo.addItem(new TableChoice(record.getId(), record.getName(), record.getStatus()));
            }
            for (ProductDAO.ProductRecord record : controller.loadProducts()) {
                if (record.isAvailable()) {
                    productCombo.addItem(new ProductChoice(Integer.parseInt(record.getId()), record.getName(), record.getPrice()));
                }
            }
        } catch (Exception exception) {
            showError("Không thể tải dữ liệu gọi món:\n" + exception.getMessage());
        }
    }

    private void addProduct() {
        ProductChoice selected = (ProductChoice) productCombo.getSelectedItem();
        if (selected == null) return;
        int quantity = (Integer) quantitySpinner.getValue();
        for (CartItem item : cart) {
            if (item.productId == selected.id) { item.quantity += quantity; refreshCart(); return; }
        }
        cart.add(new CartItem(selected.id, selected.name, selected.price, quantity));
        refreshCart();
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        double total = 0;
        for (CartItem item : cart) {
            double amount = item.quantity * item.price;
            total += amount;
            cartModel.addRow(new Object[]{item.name, item.quantity, money(item.price), money(amount)});
        }
        totalLabel.setText("Tổng: " + money(total));
    }

    private void saveOrder() {
        TableChoice table = (TableChoice) tableCombo.getSelectedItem();
        if (table == null) { showError("Vui lòng chọn bàn."); return; }
        try {
            List<OrderDAO.OrderItem> items = new ArrayList<>();
            for (CartItem item : cart) items.add(new OrderDAO.OrderItem(item.productId, item.quantity, item.price, null));
            int orderId = controller.createOrder(table.id, currentUser.getEmployeeId(), items);
            JOptionPane.showMessageDialog(this, "Đã lưu đơn hàng #" + orderId, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            cart.clear();
            refreshCart();
        } catch (Exception exception) {
            showError("Không thể lưu đơn hàng:\n" + exception.getMessage());
        }
    }

    private String money(double value) { return currency.format(value) + " đ"; }
    private JPanel whitePanel() { JPanel panel = new JPanel(); panel.setBackground(WHITE); panel.setBorder(BorderFactory.createLineBorder(BORDER)); return panel; }
    private JLabel sectionLabel(String text) { JLabel label = new JLabel(text); label.setFont(new Font("Segoe UI", Font.BOLD, 20)); label.setForeground(TEXT); return label; }
    private JLabel fieldLabel(String text) { JLabel label = new JLabel(text); label.setFont(new Font("Segoe UI", Font.BOLD, 13)); label.setForeground(TEXT); label.setAlignmentX(Component.LEFT_ALIGNMENT); return label; }
    private void showError(String message) { JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE); }

    private static class TableChoice {
        private final int id; private final String name; private final String status;
        TableChoice(int id, String name, String status) { this.id = id; this.name = name; this.status = status; }
        @Override public String toString() { return name + " - " + status; }
    }
    private static class ProductChoice {
        private final int id; private final String name; private final double price;
        ProductChoice(int id, String name, double price) { this.id = id; this.name = name; this.price = price; }
        @Override public String toString() { return name + " - " + price + " đ"; }
    }
    private static class CartItem {
        private final int productId; private final String name; private final double price; private int quantity;
        CartItem(int productId, String name, double price, int quantity) { this.productId = productId; this.name = name; this.price = price; this.quantity = quantity; }
    }
}
