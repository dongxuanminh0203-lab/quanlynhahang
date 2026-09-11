package com.nhahang.view;

import com.nhahang.controller.PaymentController;
import com.nhahang.dao.PaymentDAO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PaymentPanel extends JPanel {

    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);

    private final PaymentController controller =
            new PaymentController();
    private final NumberFormat currency =
            NumberFormat.getInstance(Locale.forLanguageTag("vi-VN"));
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new Object[]{
                            "Mã thanh toán",
                            "Mã hóa đơn",
                            "Thời gian",
                            "Số tiền",
                            "Phương thức"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable paymentTable = new JTable(tableModel);
    private final JLabel countLabel = new JLabel("Đang tải...");
    private final JLabel totalLabel = new JLabel("Tổng: 0 đ");

    public PaymentPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        buildUI();
        loadPayments();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setOpaque(false);
        main.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new javax.swing.BoxLayout(
                titleBox,
                javax.swing.BoxLayout.Y_AXIS
        ));

        JLabel title = new JLabel("Thanh toán");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Theo dõi lịch sử thanh toán từ các hóa đơn"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        titleBox.add(title);
        titleBox.add(javax.swing.Box.createVerticalStrut(5));
        titleBox.add(subtitle);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadPayments());

        header.add(titleBox, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JPanel summary = new JPanel(new BorderLayout());
        summary.setOpaque(false);
        countLabel.setForeground(TEXT_GRAY);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(new Color(37, 99, 235));
        summary.add(countLabel, BorderLayout.WEST);
        summary.add(totalLabel, BorderLayout.EAST);

        paymentTable.setRowHeight(40);
        paymentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paymentTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        paymentTable.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setPreferredSize(new Dimension(0, 420));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));

        content.add(summary, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        main.add(header, BorderLayout.NORTH);
        main.add(content, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    private void loadPayments() {
        countLabel.setText("Đang tải...");

        new SwingWorker<List<PaymentDAO.PaymentRecord>, Void>() {
            @Override
            protected List<PaymentDAO.PaymentRecord> doInBackground()
                    throws Exception {
                return controller.loadPayments();
            }

            @Override
            protected void done() {
                try {
                    List<PaymentDAO.PaymentRecord> payments = get();
                    tableModel.setRowCount(0);
                    double total = 0;

                    for (PaymentDAO.PaymentRecord payment : payments) {
                        tableModel.addRow(new Object[]{
                                payment.getPaymentId(),
                                payment.getOrderId(),
                                payment.getPaymentDate() == null
                                        ? ""
                                        : dateFormat.format(
                                                payment.getPaymentDate()
                                        ),
                                money(payment.getAmount()),
                                payment.getPaymentMethod()
                        });
                        total += payment.getAmount();
                    }

                    countLabel.setText(
                            "Có " + payments.size() + " giao dịch"
                    );
                    totalLabel.setText("Tổng: " + money(total));
                } catch (Exception exception) {
                    showError("Không thể tải lịch sử thanh toán:\n"
                            + exception.getMessage());
                }
            }
        }.execute();
    }

    private String money(double value) {
        return currency.format(value) + " đ";
    }

    private void showError(String message) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE
        );
    }
}
