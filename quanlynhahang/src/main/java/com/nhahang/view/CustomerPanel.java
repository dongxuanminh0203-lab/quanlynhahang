package com.nhahang.view;

import com.nhahang.controller.CustomerController;
import com.nhahang.dao.CustomerDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomerPanel extends JPanel {

    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color RED = new Color(220, 38, 38);

    private final CustomerController controller = new CustomerController();
    private final List<CustomerDAO.CustomerRecord> customers = new ArrayList<>();

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel countLabel;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        initUI();
        loadCustomers();
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

        JLabel title = new JLabel("Quản lý khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Quản lý thông tin khách hàng và lịch sử mua hàng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        JButton addButton = createPrimaryButton("+  Thêm khách hàng");
        addButton.addActionListener(e -> showCustomerDialog(null));

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
                new EmptyBorder(15, 18, 15, 18)
        ));

        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchLabel.setForeground(TEXT_GRAY);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm tên, số điện thoại hoặc email...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void refresh() {
                refreshTable();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refresh();
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        countLabel = new JLabel("0 khách hàng");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        countLabel.setForeground(TEXT_GRAY);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(countLabel, BorderLayout.EAST);
        center.add(toolbar, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{
                "Mã KH",
                "Họ tên",
                "Số điện thoại",
                "Email",
                "Địa chỉ",
                "Ngày tạo"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(42);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        center.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setOpaque(false);

        JButton editButton = new JButton("Sửa");
        JButton deleteButton = new JButton("Xóa");
        JButton refreshButton = new JButton("Làm mới");

        editButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        refreshButton.setFocusPainted(false);

        editButton.addActionListener(e -> editSelectedCustomer());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        refreshButton.addActionListener(e -> loadCustomers());

        bottom.add(editButton);
        bottom.add(deleteButton);
        bottom.add(refreshButton);

        center.add(bottom, BorderLayout.SOUTH);
        main.add(center, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedCustomer();
                }
            }
        });
    }

    private void loadCustomers() {
        new SwingWorker<List<CustomerDAO.CustomerRecord>, Void>() {
            @Override
            protected List<CustomerDAO.CustomerRecord> doInBackground() throws Exception {
                return controller.loadCustomers();
            }

            @Override
            protected void done() {
                try {
                    customers.clear();
                    customers.addAll(get());
                    refreshTable();
                } catch (Exception e) {
                    showError("Không thể tải danh sách khách hàng:\n" + getErrorMessage(e));
                }
            }
        }.execute();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();
        int count = 0;

        for (CustomerDAO.CustomerRecord customer : customers) {
            boolean matches = keyword.isEmpty()
                    || customer.getName().toLowerCase().contains(keyword)
                    || valueOrEmpty(customer.getPhone()).toLowerCase().contains(keyword)
                    || valueOrEmpty(customer.getEmail()).toLowerCase().contains(keyword)
                    || valueOrEmpty(customer.getAddress()).toLowerCase().contains(keyword);

            if (!matches) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    customer.getId(),
                    customer.getName(),
                    valueOrEmpty(customer.getPhone()),
                    valueOrEmpty(customer.getEmail()),
                    valueOrEmpty(customer.getAddress()),
                    formatCreatedAt(customer.getCreatedAt())
            });

            count++;
        }

        countLabel.setText(count + " khách hàng");
    }

    private void showCustomerDialog(CustomerDAO.CustomerRecord customer) {
        boolean editing = customer != null;

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        if (editing) {
            idField.setText(String.valueOf(customer.getId()));
            nameField.setText(customer.getName());
            phoneField.setText(valueOrEmpty(customer.getPhone()));
            emailField.setText(valueOrEmpty(customer.getEmail()));
            addressField.setText(valueOrEmpty(customer.getAddress()));
            idField.setEnabled(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));

        form.add(new JLabel("Mã khách hàng:"));
        form.add(idField);
        form.add(new JLabel("Họ tên:"));
        form.add(nameField);
        form.add(new JLabel("Số điện thoại:"));
        form.add(phoneField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Địa chỉ:"));
        form.add(addressField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                editing ? "Sửa khách hàng" : "Thêm khách hàng",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            CustomerDAO.CustomerRecord data = new CustomerDAO.CustomerRecord(
                    id,
                    name,
                    phone,
                    email,
                    address,
                    null
            );

            saveCustomer(data, editing);
        } catch (NumberFormatException e) {
            showError("Mã khách hàng phải là số.");
        }
    }

    private void saveCustomer(CustomerDAO.CustomerRecord customer, boolean editing) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (editing) {
                    controller.updateCustomer(customer);
                } else {
                    controller.addCustomer(customer);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            CustomerPanel.this,
                            editing ? "Cập nhật khách hàng thành công!" : "Thêm khách hàng thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadCustomers();
                } catch (Exception e) {
                    showError("Không thể lưu khách hàng:\n" + getErrorMessage(e));
                }
            }
        }.execute();
    }

    private void editSelectedCustomer() {
        int row = customerTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn khách hàng cần sửa.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int customerId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        for (CustomerDAO.CustomerRecord customer : customers) {
            if (customer.getId() == customerId) {
                showCustomerDialog(customer);
                return;
            }
        }
    }

    private void deleteSelectedCustomer() {
        int row = customerTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn khách hàng cần xóa.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int customerId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String name = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa khách hàng:\n" + name + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        final int id = customerId;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                controller.deleteCustomer(id);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(
                            CustomerPanel.this,
                            "Xóa khách hàng thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    loadCustomers();
                } catch (Exception e) {
                    showError("Không thể xóa khách hàng:\n" + getErrorMessage(e));
                }
            }
        }.execute();
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

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private String formatCreatedAt(java.sql.Timestamp createdAt) {
        if (createdAt == null) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(createdAt);
    }
}
