package com.nhahang.view;

import com.nhahang.controller.InvoiceController;
import com.nhahang.dao.InvoiceDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InvoicePanel extends JPanel {

    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color SUCCESS = new Color(22, 163, 74);

    private final InvoiceController controller =
            new InvoiceController();

    private final NumberFormat currency =
            NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));

    private DefaultTableModel invoiceModel;
    private DefaultTableModel detailModel;

    private JTable invoiceTable;
    private JTable detailTable;

    private JLabel invoiceCountLabel;
    private JLabel totalLabel;
    private JLabel selectedInvoiceLabel;

    private JButton detailButton;
    private JButton payButton;
    private JButton refreshButton;

    public InvoicePanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        buildUI();
        loadInvoices();
    }

    // =========================================================
    // GIAO DIỆN CHÍNH
    // =========================================================

    private void buildUI() {

        JPanel main = new JPanel(new BorderLayout(0, 20));
        main.setOpaque(false);
        main.setBorder(
                new EmptyBorder(28, 32, 28, 32)
        );

        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(
                new BoxLayout(
                        titleBox,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel title = new JLabel("Hóa đơn");

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Quản lý hóa đơn và xem chi tiết đơn hàng"
        );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(TEXT_GRAY);

        titleBox.add(title);
        titleBox.add(
                Box.createVerticalStrut(5)
        );
        titleBox.add(subtitle);

        main.add(
                titleBox,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // CONTENT
        // -----------------------------------------------------

        JPanel content =
                new JPanel(new BorderLayout(0, 18));

        content.setOpaque(false);

        content.add(
                createInvoicePanel(),
                BorderLayout.CENTER
        );

        content.add(
                createDetailPanel(),
                BorderLayout.SOUTH
        );

        main.add(
                content,
                BorderLayout.CENTER
        );

        add(
                main,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // DANH SÁCH HÓA ĐƠN
    // =========================================================

    private JPanel createInvoicePanel() {

        JPanel panel = whitePanel();

        panel.setLayout(
                new BorderLayout(0, 15)
        );

        panel.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        JPanel header =
                new JPanel(new BorderLayout());

        header.setOpaque(false);

        JLabel label =
                sectionLabel("Danh sách hóa đơn");

        invoiceCountLabel =
                new JLabel("Đang tải...");

        invoiceCountLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        invoiceCountLabel.setForeground(
                TEXT_GRAY
        );

        header.add(
                label,
                BorderLayout.WEST
        );

        header.add(
                invoiceCountLabel,
                BorderLayout.EAST
        );

        panel.add(
                header,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // TABLE
        // -----------------------------------------------------

        invoiceModel =
                new DefaultTableModel(
                        new Object[]{
                                "Mã HĐ",
                                "Bàn",
                                "Nhân viên",
                                "Trạng thái",
                                "Tổng tiền"
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

        invoiceTable =
                new JTable(invoiceModel);

        invoiceTable.setRowHeight(38);

        invoiceTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        invoiceTable.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        invoiceTable.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

        invoiceTable.getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (!e.getValueIsAdjusting()) {

                                int row =
                                        invoiceTable
                                                .getSelectedRow();

                                if (row >= 0) {
                                    loadSelectedInvoice(row);
                                }
                            }
                        }
                );

        JScrollPane scrollPane =
                new JScrollPane(invoiceTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        panel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        // -----------------------------------------------------
        // BUTTON
        // -----------------------------------------------------

        JPanel buttons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        buttons.setOpaque(false);

        detailButton =
                new JButton("Xem chi tiết");

        detailButton.setFocusPainted(false);

        detailButton.addActionListener(
                e -> showSelectedDetail()
        );

        payButton =
                new JButton("Thanh toán");

        payButton.setBackground(SUCCESS);
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);

        payButton.addActionListener(
                e -> paySelectedInvoice()
        );

        refreshButton =
                new JButton("Làm mới");

        refreshButton.setFocusPainted(false);

        refreshButton.addActionListener(
                e -> loadInvoices()
        );

        buttons.add(detailButton);
        buttons.add(payButton);
        buttons.add(refreshButton);

        panel.add(
                buttons,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // =========================================================
    // CHI TIẾT HÓA ĐƠN
    // =========================================================

    private JPanel createDetailPanel() {

        JPanel panel = whitePanel();

        panel.setPreferredSize(
                new Dimension(
                        0,
                        280
                )
        );

        panel.setLayout(
                new BorderLayout(0, 12)
        );

        panel.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setOpaque(false);

        selectedInvoiceLabel =
                sectionLabel(
                        "Chi tiết hóa đơn"
                );

        totalLabel =
                new JLabel("Tổng: 0 đ");

        totalLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        totalLabel.setForeground(
                PRIMARY
        );

        header.add(
                selectedInvoiceLabel,
                BorderLayout.WEST
        );

        header.add(
                totalLabel,
                BorderLayout.EAST
        );

        panel.add(
                header,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // DETAIL TABLE
        // -----------------------------------------------------

        detailModel =
                new DefaultTableModel(
                        new Object[]{
                                "Mã món",
                                "Tên món",
                                "SL",
                                "Đơn giá",
                                "Thành tiền",
                                "Ghi chú"
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

        detailTable =
                new JTable(detailModel);

        detailTable.setRowHeight(34);

        detailTable.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        detailTable.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

        JScrollPane scrollPane =
                new JScrollPane(detailTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        panel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return panel;
    }

    // =========================================================
    // LOAD HÓA ĐƠN
    // =========================================================

    private void loadInvoices() {

        refreshButton.setEnabled(false);

        invoiceCountLabel.setText(
                "Đang tải..."
        );

        new SwingWorker<
                List<InvoiceDAO.InvoiceRecord>,
                Void
                >() {

            @Override
            protected List<InvoiceDAO.InvoiceRecord>
            doInBackground()
                    throws Exception {

                return controller.loadInvoices();
            }

            @Override
            protected void done() {

                try {

                    List<InvoiceDAO.InvoiceRecord>
                            invoices = get();

                    invoiceModel.setRowCount(0);

                    double total = 0;

                    for (
                            InvoiceDAO.InvoiceRecord invoice
                            : invoices
                    ) {

                        invoiceModel.addRow(
                                new Object[]{
                                        invoice.getOrderId(),
                                        invoice.getTableName(),
                                        invoice.getEmployeeId(),
                                        displayStatus(
                                                invoice.getStatus()
                                        ),
                                        money(
                                                invoice.getTotalAmount()
                                        )
                                }
                        );

                        total +=
                                invoice.getTotalAmount();
                    }

                    invoiceCountLabel.setText(
                            "Có " +
                            invoices.size() +
                            " hóa đơn"
                    );

                    totalLabel.setText(
                            "Tổng: " +
                            money(total)
                    );

                    detailModel.setRowCount(0);

                    selectedInvoiceLabel.setText(
                            "Chi tiết hóa đơn"
                    );

                    refreshButton.setEnabled(true);

                    payButton.setEnabled(false);
                    detailButton.setEnabled(false);

                } catch (Exception exception) {

                    refreshButton.setEnabled(true);

                    showError(
                            "Không thể tải hóa đơn:\n"
                            + exception.getMessage()
                    );
                }
            }

        }.execute();
    }

    // =========================================================
    // CHỌN HÓA ĐƠN
    // =========================================================

    private void loadSelectedInvoice(
            int row
    ) {

        if (row < 0) {
            return;
        }

        int orderId =
                Integer.parseInt(
                        invoiceModel
                                .getValueAt(row, 0)
                                .toString()
                );

        String status =
                invoiceModel
                        .getValueAt(row, 3)
                        .toString();

        detailButton.setEnabled(true);

        payButton.setEnabled(
                "CHƯA THANH TOÁN".equals(status)
        );

        selectedInvoiceLabel.setText(
                "Chi tiết hóa đơn #" +
                orderId
        );

        loadDetails(orderId);
    }

    // =========================================================
    // LOAD CHI TIẾT
    // =========================================================

    private void loadDetails(
            int orderId
    ) {

        detailModel.setRowCount(0);

        new SwingWorker<
                List<InvoiceDAO.InvoiceDetail>,
                Void
                >() {

            @Override
            protected List<InvoiceDAO.InvoiceDetail>
            doInBackground()
                    throws Exception {

                return controller.loadDetails(
                        orderId
                );
            }

            @Override
            protected void done() {

                try {

                    List<InvoiceDAO.InvoiceDetail>
                            details = get();

                    double total = 0;

                    for (
                            InvoiceDAO.InvoiceDetail detail
                            : details
                    ) {

                        double amount =
                                detail.getAmount();

                        total += amount;

                        detailModel.addRow(
                                new Object[]{
                                        detail.getProductId(),
                                        detail.getProductName(),
                                        detail.getQuantity(),
                                        money(
                                                detail.getUnitPrice()
                                        ),
                                        money(amount),
                                        detail.getNote() == null
                                                ? ""
                                                : detail.getNote()
                                }
                        );
                    }

                    totalLabel.setText(
                            "Tổng: " +
                            money(total)
                    );

                } catch (Exception exception) {

                    showError(
                            "Không thể tải chi tiết hóa đơn:\n"
                            + exception.getMessage()
                    );
                }
            }

        }.execute();
    }

    // =========================================================
    // XEM CHI TIẾT
    // =========================================================

    private void showSelectedDetail() {

        int row =
                invoiceTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một hóa đơn.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        loadSelectedInvoice(row);
    }

    // =========================================================
    // THANH TOÁN
    // =========================================================

    private void paySelectedInvoice() {

        int row =
                invoiceTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn hóa đơn cần thanh toán.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int orderId =
                Integer.parseInt(
                        invoiceModel
                                .getValueAt(row, 0)
                                .toString()
                );

        String tableName =
                invoiceModel
                        .getValueAt(row, 1)
                        .toString();

        double amount =
                parseMoney(
                        invoiceModel
                                .getValueAt(row, 4)
                                .toString()
                );

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        createPaymentForm(
                                orderId,
                                tableName,
                                amount
                        ),
                        "Xác nhận thanh toán",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                confirm
                != JOptionPane.YES_OPTION
        ) {
            return;
        }

        String paymentMethod =
                selectedPaymentMethod;

        // Lấy table_id từ dữ liệu hóa đơn.
        // Cột table_id không hiển thị trên JTable
        // nên lấy lại từ database qua danh sách hiện tại.

        payButton.setEnabled(false);

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws Exception {

                List<InvoiceDAO.InvoiceRecord>
                        invoices =
                        controller.loadInvoices();

                for (
                        InvoiceDAO.InvoiceRecord invoice
                        : invoices
                ) {

                    if (
                            invoice.getOrderId()
                            == orderId
                    ) {

                        controller.payInvoice(
                                invoice.getOrderId(),
                                invoice.getTableId(),
                                amount,
                                paymentMethod
                        );

                        break;
                    }
                }

                return null;
            }

            @Override
            protected void done() {

                try {

                    get();

                    JOptionPane.showMessageDialog(
                            InvoicePanel.this,
                            "Thanh toán hóa đơn #"
                                    + orderId
                                    + " thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadInvoices();

                } catch (Exception exception) {

                    showError(
                            "Thanh toán thất bại:\n"
                            + exception.getMessage()
                    );

                    payButton.setEnabled(true);
                }
            }

        }.execute();
    }

    // =========================================================
    // CHUYỂN TRẠNG THÁI
    // =========================================================

    private String displayStatus(
            String status
    ) {

        if (status == null) {
            return "";
        }

        if (
                "PAID".equalsIgnoreCase(status)
        ) {
            return "ĐÃ THANH TOÁN";
        }

        if (
                "OPEN".equalsIgnoreCase(status)
        ) {
            return "CHƯA THANH TOÁN";
        }

        return status;
    }

    // =========================================================
    // TIỀN
    // =========================================================

    private String money(
            double value
    ) {

        return currency.format(value)
                + " đ";
    }

    private double parseMoney(
            String value
    ) {

        if (value == null) {
            return 0;
        }

        String clean =
                value
                        .replace("đ", "")
                        .replace(".", "")
                        .replace(",", "")
                        .trim();

        try {

            return Double.parseDouble(clean);

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    private String selectedPaymentMethod = "Tiền mặt";

    private JPanel createPaymentForm(
            int orderId,
            String tableName,
            double amount
    ) {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        JComboBox<String> methodBox = new JComboBox<>(
                new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"}
        );

        form.add(new JLabel("Hóa đơn #" + orderId));
        form.add(new JLabel("Bàn: " + tableName));
        form.add(new JLabel("Tổng tiền:"));
        form.add(new JLabel(money(amount)));
        form.add(new JLabel("Phương thức:"));
        form.add(methodBox);

        methodBox.addActionListener(e ->
                selectedPaymentMethod =
                        String.valueOf(methodBox.getSelectedItem())
        );

        selectedPaymentMethod = "Tiền mặt";
        return form;
    }

    // =========================================================
    // UI HELPER
    // =========================================================

    private JPanel whitePanel() {

        JPanel panel = new JPanel();

        panel.setBackground(WHITE);

        panel.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        return panel;
    }

    private JLabel sectionLabel(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        label.setForeground(TEXT);

        return label;
    }

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
}