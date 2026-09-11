package com.nhahang.view;

import com.nhahang.controller.EmployeeController;
import com.nhahang.dao.EmployeeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeePanel extends JPanel {

    // =========================
    // MÀU
    // =========================

    private static final Color BACKGROUND =
            new Color(246, 248, 252);

    private static final Color WHITE =
            Color.WHITE;

    private static final Color TEXT =
            new Color(25, 32, 45);

    private static final Color TEXT_GRAY =
            new Color(107, 118, 135);

    private static final Color BORDER =
            new Color(225, 229, 236);

    private static final Color PRIMARY =
            new Color(37, 99, 235);

    private static final Color GREEN =
            new Color(22, 163, 74);

    private static final Color RED =
            new Color(220, 38, 38);

    // =========================
    // DATA
    // =========================

    private final EmployeeController controller =
            new EmployeeController();

    private final List<EmployeeDAO.EmployeeRecord>
            employees = new ArrayList<>();

    // =========================
    // COMPONENT
    // =========================

    private JTable employeeTable;

    private DefaultTableModel tableModel;

    private JTextField searchField;

    private JLabel countLabel;

    // =========================
    // CONSTRUCTOR
    // =========================

    public EmployeePanel() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );

        initUI();

        loadEmployees();
    }

    // =========================================================
    // UI
    // =========================================================

    private void initUI() {

        JPanel main =
                new JPanel(
                        new BorderLayout()
                );

        main.setOpaque(false);

        main.setBorder(
                new EmptyBorder(
                        30,
                        36,
                        30,
                        36
                )
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setOpaque(false);

        JPanel titlePanel =
                new JPanel();

        titlePanel.setOpaque(false);

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title =
                new JLabel(
                        "Quản lý nhân viên"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
                )
        );

        title.setForeground(
                TEXT
        );

        JLabel subtitle =
                new JLabel(
                        "Quản lý thông tin và trạng thái nhân viên"
                );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(
                TEXT_GRAY
        );

        titlePanel.add(title);

        titlePanel.add(
                Box.createVerticalStrut(6)
        );

        titlePanel.add(subtitle);

        JButton addButton =
                createPrimaryButton(
                        "+  Thêm nhân viên"
                );

        addButton.addActionListener(
                e -> showEmployeeDialog(null)
        );

        header.add(
                titlePanel,
                BorderLayout.WEST
        );

        header.add(
                addButton,
                BorderLayout.EAST
        );

        main.add(
                header,
                BorderLayout.NORTH
        );

        // =====================================================
        // CENTER
        // =====================================================

        JPanel center =
                new JPanel(
                        new BorderLayout(0, 18)
                );

        center.setOpaque(false);

        center.setBorder(
                new EmptyBorder(
                        28,
                        0,
                        0,
                        0
                )
        );

        // =====================================================
        // TOOLBAR
        // =====================================================

        JPanel toolbar =
                new JPanel(
                        new BorderLayout()
                );

        toolbar.setBackground(
                WHITE
        );

        toolbar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                15,
                                18,
                                15,
                                18
                        )
                )
        );

        JLabel searchLabel =
                new JLabel(
                        "Tìm kiếm:"
                );

        searchLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        searchLabel.setForeground(
                TEXT_GRAY
        );

        searchField =
                new JTextField();

        searchField.setPreferredSize(
                new Dimension(
                        300,
                        40
                )
        );

        searchField.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm tên hoặc số điện thoại..."
        );

        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            private void refresh() {
                                refreshTable();
                            }

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {
                                refresh();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {
                                refresh();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {
                                refresh();
                            }
                        }
                );

        JPanel searchPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        searchPanel.setOpaque(false);

        searchPanel.add(
                searchLabel
        );

        searchPanel.add(
                searchField
        );

        countLabel =
                new JLabel(
                        "0 nhân viên"
                );

        countLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        countLabel.setForeground(
                TEXT_GRAY
        );

        toolbar.add(
                searchPanel,
                BorderLayout.WEST
        );

        toolbar.add(
                countLabel,
                BorderLayout.EAST
        );

        center.add(
                toolbar,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLE
        // =====================================================

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "Mã NV",
                                "Họ tên",
                                "Số điện thoại",
                                "Chức vụ",
                                "Trạng thái"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        employeeTable =
                new JTable(
                        tableModel
                );

        employeeTable.setRowHeight(
                42
        );

        employeeTable.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        employeeTable.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

        employeeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        employeeTable
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        center.add(
                scrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                10
                        )
                );

        bottom.setOpaque(false);

        JButton editButton =
                new JButton(
                        "Sửa"
                );

        JButton deleteButton =
                new JButton(
                        "Xóa"
                );

        JButton refreshButton =
                new JButton(
                        "Làm mới"
                );

        editButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        refreshButton.setFocusPainted(false);

        editButton.addActionListener(
                e -> editSelectedEmployee()
        );

        deleteButton.addActionListener(
                e -> deleteSelectedEmployee()
        );

        refreshButton.addActionListener(
                e -> loadEmployees()
        );

        bottom.add(
                editButton
        );

        bottom.add(
                deleteButton
        );

        bottom.add(
                refreshButton
        );

        center.add(
                bottom,
                BorderLayout.SOUTH
        );

        main.add(
                center,
                BorderLayout.CENTER
        );

        add(
                main,
                BorderLayout.CENTER
        );

        // Double click để sửa
        employeeTable.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        if (
                                e.getClickCount() == 2
                        ) {

                            editSelectedEmployee();
                        }
                    }
                }
        );
    }

    // =========================================================
    // LOAD DATA
    // =========================================================

    private void loadEmployees() {

        new SwingWorker<
                List<EmployeeDAO.EmployeeRecord>,
                Void
                >() {

            @Override
            protected List<EmployeeDAO.EmployeeRecord>
            doInBackground()
                    throws Exception {

                return controller.loadEmployees();
            }

            @Override
            protected void done() {

                try {

                    employees.clear();

                    employees.addAll(
                            get()
                    );

                    refreshTable();

                } catch (Exception e) {

                    showError(
                            "Không thể tải danh sách nhân viên:\n"
                                    + getErrorMessage(e)
                    );
                }
            }

        }.execute();
    }

    // =========================================================
    // REFRESH TABLE
    // =========================================================

    private void refreshTable() {

        tableModel.setRowCount(0);

        String keyword =
                searchField == null
                        ? ""
                        : searchField
                        .getText()
                        .trim()
                        .toLowerCase();

        int count = 0;

        for (
                EmployeeDAO.EmployeeRecord employee
                : employees
        ) {

            boolean matches =
                    keyword.isEmpty()
                            || employee.getName()
                            .toLowerCase()
                            .contains(keyword)
                            || employee.getPhone()
                            .toLowerCase()
                            .contains(keyword);

            if (!matches) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            employee.getId(),
                            employee.getName(),
                            employee.getPhone(),
                            employee.getPosition(),
                            employee.isActive()
                                    ? "Đang làm"
                                    : "Nghỉ"
                    }
            );

            count++;
        }

        countLabel.setText(
                count + " nhân viên"
        );
    }

    // =========================================================
    // THÊM / SỬA
    // =========================================================

    private void showEmployeeDialog(
            EmployeeDAO.EmployeeRecord employee
    ) {

        boolean editing =
                employee != null;

        JTextField idField =
                new JTextField();

        JTextField nameField =
                new JTextField();

        JTextField phoneField =
                new JTextField();

        JTextField positionField =
                new JTextField();

        JCheckBox statusBox =
                new JCheckBox(
                        "Đang làm việc"
                );

        if (editing) {

            idField.setText(
                    String.valueOf(
                            employee.getId()
                    )
            );

            nameField.setText(
                    employee.getName()
            );

            phoneField.setText(
                    employee.getPhone()
            );

            positionField.setText(
                    employee.getPosition()
            );

            statusBox.setSelected(
                    employee.isActive()
            );

            idField.setEnabled(false);

        } else {

            statusBox.setSelected(true);
        }

        JPanel form =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                10,
                                12
                        )
                );

        form.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        form.add(
                new JLabel("Mã nhân viên:")
        );

        form.add(
                idField
        );

        form.add(
                new JLabel("Họ tên:")
        );

        form.add(
                nameField
        );

        form.add(
                new JLabel("Số điện thoại:")
        );

        form.add(
                phoneField
        );

        form.add(
                new JLabel("Chức vụ:")
        );

        form.add(
                positionField
        );

        form.add(
                new JLabel("Trạng thái:")
        );

        form.add(
                statusBox
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        form,
                        editing
                                ? "Sửa nhân viên"
                                : "Thêm nhân viên",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (
                result
                != JOptionPane.OK_OPTION
        ) {
            return;
        }

        try {

            int id =
                    Integer.parseInt(
                            idField
                                    .getText()
                                    .trim()
                    );

            String name =
                    nameField
                            .getText()
                            .trim();

            String phone =
                    phoneField
                            .getText()
                            .trim();

            String position =
                    positionField
                            .getText()
                            .trim();

            EmployeeDAO.EmployeeRecord data =
                    new EmployeeDAO.EmployeeRecord(
                            id,
                            name,
                            phone,
                            position,
                            statusBox.isSelected()
                    );

            saveEmployee(
                    data,
                    editing
            );

        } catch (
                NumberFormatException e
        ) {

            showError("Mã nhân viên phải là số.");
        }
    }

    // =========================================================
    // SAVE
    // =========================================================

    private void saveEmployee(
            EmployeeDAO.EmployeeRecord employee,
            boolean editing
    ) {

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws Exception {

                if (editing) {

                    controller.updateEmployee(
                            employee
                    );

                } else {

                    controller.addEmployee(
                            employee
                    );
                }

                return null;
            }

            @Override
            protected void done() {

                try {

                    get();

                    JOptionPane.showMessageDialog(
                            EmployeePanel.this,
                            editing
                                    ? "Cập nhật nhân viên thành công!"
                                    : "Thêm nhân viên thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadEmployees();

                } catch (Exception e) {

                    showError(
                            "Không thể lưu nhân viên:\n"
                                    + getErrorMessage(e)
                    );
                }
            }

        }.execute();
    }

    // =========================================================
    // SỬA
    // =========================================================

    private void editSelectedEmployee() {

        int row =
                employeeTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn nhân viên cần sửa.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int employeeId =
                Integer.parseInt(
                        tableModel
                                .getValueAt(
                                        row,
                                        0
                                )
                                .toString()
                );

        EmployeeDAO.EmployeeRecord selected =
                null;

        for (
                EmployeeDAO.EmployeeRecord employee
                : employees
        ) {

            if (
                    employee.getId()
                    == employeeId
            ) {

                selected = employee;
                break;
            }
        }

        if (selected != null) {

            showEmployeeDialog(
                    selected
            );
        }
    }

    // =========================================================
    // XÓA
    // =========================================================

    private void deleteSelectedEmployee() {

        int row =
                employeeTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn nhân viên cần xóa.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int employeeId =
                Integer.parseInt(
                        tableModel
                                .getValueAt(
                                        row,
                                        0
                                )
                                .toString()
                );

        String name =
                tableModel
                        .getValueAt(
                                row,
                                1
                        )
                        .toString();

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa nhân viên:\n"
                                + name
                                + "?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                confirm
                != JOptionPane.YES_OPTION
        ) {
            return;
        }

        final int id = employeeId;

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws Exception {

                controller.deleteEmployee(id);

                return null;
            }

            @Override
            protected void done() {

                try {

                    get();

                    JOptionPane.showMessageDialog(
                            EmployeePanel.this,
                            "Xóa nhân viên thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadEmployees();

                } catch (Exception e) {

                    showError(
                            "Không thể xóa nhân viên:\n"
                                    + getErrorMessage(e)
                    );
                }
            }

        }.execute();
    }

    // =========================================================
    // BUTTON
    // =========================================================

    private JButton createPrimaryButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                PRIMARY
        );

        button.setFocusPainted(
                false
        );

        button.setBorder(
                new EmptyBorder(
                        11,
                        18,
                        11,
                        18
                )
        );

        return button;
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String getErrorMessage(
            Exception e
    ) {

        Throwable cause = e;

        while (
                cause.getCause() != null
        ) {

            cause = cause.getCause();
        }

        return cause.getMessage() != null
                ? cause.getMessage()
                : cause.toString();
    }
}