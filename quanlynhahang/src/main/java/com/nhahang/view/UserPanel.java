package com.nhahang.view;

import com.nhahang.controller.UserController;
import com.nhahang.dao.EmployeeDAO;
import com.nhahang.model.Employee;
import com.nhahang.model.UserRecord;
import com.nhahang.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserPanel extends JPanel {

    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color PRIMARY = new Color(37, 99, 235);

    private final UserController controller = new UserController();
    private final List<UserRecord> users = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();

    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel countLabel;

    public UserPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(30, 36, 30, 36));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản lý tài khoản");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Quản lý tài khoản đăng nhập và phân quyền hệ thống");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        JButton addButton = createPrimaryButton("+  Thêm tài khoản");
        addButton.addActionListener(e -> showUserDialog(null));
        header.add(titlePanel, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(28, 0, 0, 0));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 18, 15, 18)));
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchLabel.setForeground(TEXT_GRAY);
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(330, 40));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 12, 8, 12)));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm tên tài khoản, vai trò hoặc nhân viên...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void refresh() { refreshTable(); }
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }
        });
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        countLabel = new JLabel("0 tài khoản");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(TEXT_GRAY);
        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(countLabel, BorderLayout.EAST);
        center.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "Mã", "Tên tài khoản", "Vai trò", "Nhân viên liên kết", "Trạng thái", "Ngày tạo"
        }, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(42);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        center.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);
        JButton editButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm mới");
        editButton.addActionListener(e -> editSelectedUser());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        refreshButton.addActionListener(e -> loadData());
        bottom.add(editButton);
        bottom.add(deleteButton);
        bottom.add(refreshButton);
        center.add(bottom, BorderLayout.SOUTH);
        main.add(center, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) editSelectedUser();
            }
        });
    }

    private void loadData() {
        new SwingWorker<Object[], Void>() {
            protected Object[] doInBackground() throws Exception {
                return new Object[]{controller.loadUsers(), new EmployeeDAO().findAll()};
            }
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] data = get();
                    users.clear();
                    users.addAll((List<UserRecord>) data[0]);
                    employees.clear();
                    employees.addAll((List<Employee>) data[1]);
                    refreshTable();
                } catch (Exception e) {
                    showError("Không thể tải danh sách tài khoản:\n" + getErrorMessage(e));
                }
            }
        }.execute();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        int count = 0;
        for (UserRecord user : users) {
            String employeeName = valueOrEmpty(user.getEmployeeName());
            boolean matches = keyword.isEmpty()
                    || user.getUsername().toLowerCase().contains(keyword)
                    || user.getRole().toLowerCase().contains(keyword)
                    || employeeName.toLowerCase().contains(keyword);
            if (!matches) continue;
            tableModel.addRow(new Object[]{user.getId(), user.getUsername(), user.getRole(),
                    employeeName.isEmpty() ? "Chưa liên kết" : employeeName,
                    user.isActive() ? "Đang hoạt động" : "Đã khóa",
                    formatCreatedAt(user.getCreatedAt())});
            count++;
        }
        countLabel.setText(count + " tài khoản");
    }

    private void showUserDialog(UserRecord user) {
        boolean editing = user != null;
        JTextField usernameField = new JTextField(editing ? user.getUsername() : "");
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"ADMIN", "STAFF"});
        JComboBox<EmployeeOption> employeeBox = new JComboBox<>();
        JCheckBox activeBox = new JCheckBox("Tài khoản đang hoạt động", !editing || user.isActive());
        if (editing) roleBox.setSelectedItem(user.getRole());
        employeeBox.addItem(new EmployeeOption(null, "Không liên kết"));
        for (Employee employee : employees) {
            employeeBox.addItem(new EmployeeOption(employee.getId(), employee.getId() + " - " + employee.getName()));
        }
        if (editing && user.getEmployeeId() != null) {
            for (int i = 0; i < employeeBox.getItemCount(); i++) {
                if (user.getEmployeeId().equals(employeeBox.getItemAt(i).id)) employeeBox.setSelectedIndex(i);
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Tên tài khoản:"));
        form.add(usernameField);
        form.add(new JLabel(editing ? "Mật khẩu mới:" : "Mật khẩu:"));
        form.add(passwordField);
        form.add(new JLabel("Vai trò:"));
        form.add(roleBox);
        form.add(new JLabel("Nhân viên:"));
        form.add(employeeBox);
        form.add(new JLabel("Trạng thái:"));
        form.add(activeBox);

        int result = JOptionPane.showConfirmDialog(this, form,
                editing ? "Sửa tài khoản" : "Thêm tài khoản",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String password = new String(passwordField.getPassword()).trim();
        EmployeeOption employee = (EmployeeOption) employeeBox.getSelectedItem();
        UserRecord data = new UserRecord(
                editing ? user.getId() : 0,
                usernameField.getText().trim(), password, roleBox.getSelectedItem().toString(),
                employee == null ? null : employee.id, activeBox.isSelected(), null,
                editing ? user.getEmployeeName() : null);
        saveUser(data, editing);
    }

    private void saveUser(UserRecord user, boolean editing) {
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception {
                if (editing) controller.updateUser(user); else controller.addUser(user);
                return null;
            }
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(UserPanel.this, "Lưu tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } catch (Exception e) {
                    showError("Không thể lưu tài khoản:\n" + getErrorMessage(e));
                }
            }
        }.execute();
    }

    private void editSelectedUser() {
        UserRecord user = selectedUser();
        if (user == null) return;
        showUserDialog(user);
    }

    private void deleteSelectedUser() {
        UserRecord user = selectedUser();
        if (user == null) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa tài khoản " + user.getUsername() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        new SwingWorker<Void, Void>() {
            protected Void doInBackground() throws Exception { controller.deleteUser(user.getId()); return null; }
            protected void done() {
                try { get(); loadData(); }
                catch (Exception e) { showError("Không thể xóa tài khoản:\n" + getErrorMessage(e)); }
            }
        }.execute();
    }

    private UserRecord selectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        for (UserRecord user : users) if (user.getId() == id) return user;
        return null;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 18, 11, 18));
        return button;
    }

    private String valueOrEmpty(String value) { return value == null ? "" : value; }

    private String formatCreatedAt(java.sql.Timestamp createdAt) {
        return createdAt == null ? "" : new SimpleDateFormat("dd/MM/yyyy HH:mm").format(createdAt);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private String getErrorMessage(Exception e) {
        Throwable cause = e;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() == null ? cause.toString() : cause.getMessage();
    }

    private static class EmployeeOption {
        private final Integer id;
        private final String label;
        private EmployeeOption(Integer id, String label) { this.id = id; this.label = label; }
        public String toString() { return label; }
    }
}
