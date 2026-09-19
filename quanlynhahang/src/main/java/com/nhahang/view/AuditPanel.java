package com.nhahang.view;

import com.nhahang.dao.AuditDAO;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class AuditPanel extends JPanel {

    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);

    private final AuditDAO auditDAO = new AuditDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Thời gian", "Người thao tác", "Hành động", "Đối tượng", "Mã", "Chi tiết"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTextField searchField = new JTextField();
    private final JLabel countLabel = new JLabel("0 bản ghi");

    public AuditPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(30, 36, 30, 36));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new javax.swing.BoxLayout(titlePanel, javax.swing.BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Nhật ký hoạt động");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Theo dõi các thao tác quan trọng trong hệ thống");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);
        titlePanel.add(title);
        titlePanel.add(javax.swing.Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.addActionListener(event -> loadData());
        header.add(titlePanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(24, 0, 0, 0));

        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 16, 12, 16)));
        searchField.setPreferredSize(new Dimension(360, 38));
        searchField.putClientProperty("JTextField.placeholderText", "Tìm hành động, người dùng hoặc chi tiết...");
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.addActionListener(event -> loadData());
        searchField.addActionListener(event -> loadData());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        countLabel.setForeground(TEXT_GRAY);
        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(countLabel, BorderLayout.EAST);
        content.add(toolbar, BorderLayout.NORTH);

        JTable auditTable = new JTable(tableModel);
        auditTable.setRowHeight(38);
        auditTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        auditTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        auditTable.setAutoCreateRowSorter(true);
        auditTable.setFillsViewportHeight(true);
        auditTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        auditTable.getColumnModel().getColumn(5).setPreferredWidth(360);
        content.add(new JScrollPane(auditTable), BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
    }

    private void loadData() {
        String keyword = searchField.getText();
        new SwingWorker<List<AuditDAO.AuditRecord>, Void>() {
            @Override
            protected List<AuditDAO.AuditRecord> doInBackground() throws Exception {
                return auditDAO.findRecent(keyword);
            }

            @Override
            protected void done() {
                try {
                    List<AuditDAO.AuditRecord> records = get();
                    tableModel.setRowCount(0);
                    for (AuditDAO.AuditRecord record : records) {
                        tableModel.addRow(new Object[]{
                                record.createdAt(),
                                record.actor(),
                                record.action(),
                                record.entity(),
                                record.entityId() == null ? "" : record.entityId(),
                                record.details()
                        });
                    }
                    countLabel.setText(records.size() + " bản ghi");
                } catch (Exception exception) {
                    countLabel.setText("Không tải được dữ liệu");
                    showError("Không thể tải nhật ký hoạt động: " + exception.getMessage());
                }
            }
        }.execute();
    }

    private void showError(String message) {
        javax.swing.JOptionPane.showMessageDialog(
                this, message, "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
}
