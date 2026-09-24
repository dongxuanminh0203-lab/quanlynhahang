package com.nhahang.view;

import com.nhahang.controller.IngredientController;
import com.nhahang.model.Ingredient;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class IngredientPanel extends JPanel {
    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private final IngredientController controller = new IngredientController();
    private final List<Ingredient> ingredients = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel summary;

    public IngredientPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        buildUI();
        loadIngredients();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Kho nguyên liệu");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Theo dõi tồn kho và nguyên liệu dùng cho các món ăn");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        titleBox.add(title);
        titleBox.add(subtitle);
        JButton add = primaryButton("+  Thêm nguyên liệu");
        add.addActionListener(event -> showIngredientDialog(null));
        heading.add(titleBox, BorderLayout.WEST);
        heading.add(add, BorderLayout.EAST);
        main.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        tableModel = new DefaultTableModel(
                new Object[]{"Mã", "Nguyên liệu", "Đơn vị", "Tồn kho", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(38);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        summary = new JLabel("Đang tải...");
        summary.setForeground(PRIMARY);
        summary.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton edit = new JButton("Sửa");
        edit.addActionListener(event -> editSelected());
        JButton delete = new JButton("Xóa");
        delete.addActionListener(event -> deleteSelected());
        JButton refresh = new JButton("Làm mới");
        refresh.addActionListener(event -> loadIngredients());
        actions.add(edit);
        actions.add(delete);
        actions.add(refresh);
        bottom.add(summary, BorderLayout.WEST);
        bottom.add(actions, BorderLayout.EAST);
        content.add(bottom, BorderLayout.SOUTH);
        main.add(content, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private void loadIngredients() {
        summary.setText("Đang tải kho nguyên liệu...");
        new SwingWorker<List<Ingredient>, Void>() {
            @Override protected List<Ingredient> doInBackground() throws Exception {
                return controller.loadIngredients();
            }
            @Override protected void done() {
                try {
                    ingredients.clear();
                    ingredients.addAll(get());
                    refreshTable();
                } catch (Exception exception) {
                    summary.setText("Không thể tải kho: " + rootMessage(exception));
                }
            }
        }.execute();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Ingredient ingredient : ingredients) {
            tableModel.addRow(new Object[]{
                    ingredient.getId(), ingredient.getName(), ingredient.getUnit(),
                    formatQuantity(ingredient.getStockQuantity()),
                    ingredient.isActive() ? "Đang sử dụng" : "Tạm ngưng"
            });
        }
        summary.setText(ingredients.size() + " nguyên liệu");
    }

    private void showIngredientDialog(Ingredient editing) {
        boolean isEditing = editing != null;
        JDialog dialog = new JDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                isEditing ? "Sửa nguyên liệu" : "Thêm nguyên liệu",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(430, 330);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        JTextField name = new JTextField(isEditing ? editing.getName() : "");
        JTextField unit = new JTextField(isEditing ? editing.getUnit() : "kg");
        JTextField stock = new JTextField(isEditing ? String.valueOf(editing.getStockQuantity()) : "0");
        JCheckBox active = new JCheckBox("Đang sử dụng", !isEditing || editing.isActive());
        form.add(new JLabel("Tên nguyên liệu"));
        form.add(name);
        form.add(new JLabel("Đơn vị (kg, lít, cái...)"));
        form.add(unit);
        form.add(new JLabel("Số lượng tồn"));
        form.add(stock);
        if (isEditing) form.add(active);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Hủy");
        JButton save = primaryButton(isEditing ? "Lưu thay đổi" : "Thêm");
        cancel.addActionListener(event -> dialog.dispose());
        save.addActionListener(event -> {
            try {
                double quantity = Double.parseDouble(stock.getText().trim());
                if (isEditing) {
                    controller.editIngredient(editing.getId(), name.getText(), unit.getText(),
                            quantity, active.isSelected());
                } else {
                    controller.addIngredient(name.getText(), unit.getText(), quantity);
                }
                dialog.dispose();
                loadIngredients();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(dialog, rootMessage(exception), "Dữ liệu không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        buttons.add(cancel);
        buttons.add(save);
        JPanel root = new JPanel(new BorderLayout());
        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row >= 0 && row < ingredients.size()) showIngredientDialog(ingredients.get(row));
        else showMessage("Vui lòng chọn nguyên liệu cần sửa.");
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= ingredients.size()) {
            showMessage("Vui lòng chọn nguyên liệu cần xóa.");
            return;
        }
        Ingredient ingredient = ingredients.get(row);
        int answer = JOptionPane.showConfirmDialog(this,
                "Xóa nguyên liệu '" + ingredient.getName() + "'?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                controller.removeIngredient(ingredient.getId());
                loadIngredients();
            } catch (Exception exception) {
                showMessage(rootMessage(exception));
            }
        }
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private String formatQuantity(double value) {
        DecimalFormat format = new DecimalFormat(
                "0.###",
                DecimalFormatSymbols.getInstance(Locale.US)
        );
        return format.format(value);
    }
    private void showMessage(String message) { JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.WARNING_MESSAGE); }
    private String rootMessage(Exception exception) {
        Throwable cause = exception;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }
}
