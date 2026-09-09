package com.nhahang.view;

import com.nhahang.controller.TableController;
import com.nhahang.dao.TableDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TablePanel extends JPanel {

    // =========================================================
    // COLORS
    // =========================================================

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

    private static final Color PRIMARY_HOVER =
            new Color(29, 78, 216);

    private static final Color GREEN =
            new Color(34, 197, 94);

    private static final Color GREEN_LIGHT =
            new Color(240, 253, 244);

    private static final Color RED =
            new Color(239, 68, 68);

    private static final Color RED_LIGHT =
            new Color(254, 242, 242);

    private static final Color GRAY_LIGHT =
            new Color(241, 245, 249);

    // =========================================================
    // COMPONENTS
    // =========================================================

    private JPanel tableContainer;
    private JTextField searchField;
    private JComboBox<String> statusCombo;

    // =========================================================
    // DATA DEMO
    // =========================================================

        private final List<TableInfo> tables = new ArrayList<>();
        private final TableController tableController = new TableController();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TablePanel() {

        setLayout(new BorderLayout());

        setBackground(BACKGROUND);

        initUI();

        loadTables();
    }

    // =========================================================
    // INIT UI
    // =========================================================

    private void initUI() {

        JPanel main =
                new JPanel(new BorderLayout());

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
                new JPanel(new BorderLayout());

        header.setOpaque(false);

        // LEFT HEADER

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
                new JLabel("Quản lý bàn");

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
                )
        );

        title.setForeground(TEXT);

        JLabel subtitle =
                new JLabel(
                        "Quản lý trạng thái và hoạt động của các bàn trong nhà hàng"
                );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(TEXT_GRAY);

        subtitle.setBorder(
                new EmptyBorder(
                        6,
                        0,
                        0,
                        0
                )
        );

        titlePanel.add(title);

        titlePanel.add(subtitle);

        header.add(
                titlePanel,
                BorderLayout.WEST
        );

        // RIGHT BUTTON

        JButton addButton =
                createPrimaryButton(
                        "+  Thêm bàn"
                );

        addButton.addActionListener(
                e -> showAddTableDialog()
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
                        new BorderLayout()
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

        toolbar.setBackground(WHITE);

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

        // SEARCH

        JPanel searchPanel =
                new JPanel(
                        new BorderLayout()
                );

        searchPanel.setOpaque(false);

        JPanel searchIcon = createSearchIcon();

        searchIcon.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        0,
                        8
                )
        );

        searchField =
                new JTextField();

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

        searchField.setPreferredSize(
                new Dimension(
                        280,
                        40
                )
        );

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm kiếm bàn..."
        );

        searchField.addActionListener(
                e -> loadTables()
        );

        searchPanel.add(
                searchIcon,
                BorderLayout.WEST
        );

        searchPanel.add(
                searchField,
                BorderLayout.CENTER
        );

        toolbar.add(
                searchPanel,
                BorderLayout.WEST
        );

        // FILTER

        JPanel filterPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        filterPanel.setOpaque(false);

        JLabel filterLabel =
                new JLabel("Trạng thái:");

        filterLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        filterLabel.setForeground(TEXT_GRAY);

        statusCombo =
                new JComboBox<>(
                        new String[]{
                                "Tất cả",
                                "Trống",
                                "Đang phục vụ"
                        }
                );

        statusCombo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        statusCombo.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        statusCombo.addActionListener(
                e -> loadTables()
        );

        filterPanel.add(filterLabel);

        filterPanel.add(statusCombo);

        toolbar.add(
                filterPanel,
                BorderLayout.EAST
        );

        center.add(
                toolbar,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLE CONTAINER
        // =====================================================

        tableContainer =
                new JPanel();

        tableContainer.setOpaque(false);

        tableContainer.setLayout(
                new GridLayout(
                        0,
                        4,
                        18,
                        18
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        tableContainer
                );

        scrollPane.setBorder(null);

        scrollPane.setOpaque(false);

        scrollPane.getViewport()
                .setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.getHorizontalScrollBar().setUnitIncrement(24);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        center.add(
                scrollPane,
                BorderLayout.CENTER
        );

        main.add(
                center,
                BorderLayout.CENTER
        );

        add(
                main,
                BorderLayout.CENTER
        );
    }

        private JPanel createSearchIcon() {
                JPanel icon = new JPanel() {
                        @Override
                        protected void paintComponent(Graphics graphics) {
                                super.paintComponent(graphics);
                                Graphics2D g2 = (Graphics2D) graphics.create();
                                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                                RenderingHints.VALUE_ANTIALIAS_ON);
                                g2.setColor(TEXT_GRAY);
                                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                                                BasicStroke.JOIN_ROUND));
                                g2.drawOval(5, 5, 11, 11);
                                g2.drawLine(14, 14, 20, 20);
                                g2.dispose();
                        }
                };
                icon.setOpaque(false);
                icon.setPreferredSize(new Dimension(26, 26));
                return icon;
        }

    // =========================================================
    // LOAD TABLES
    // =========================================================

    private void loadTables() {

                try {
                        tables.clear();
                        for (TableDAO.TableRecord record : tableController.loadTables()) {
                                tables.add(new TableInfo(
                                                record.getId(),
                                                record.getName(),
                                                record.getCapacity(),
                                                record.getStatus()
                                ));
                        }
                } catch (Exception exception) {
                        tables.clear();
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Không thể tải dữ liệu bàn:\n" + exception.getMessage(),
                                        "Lỗi kết nối cơ sở dữ liệu",
                                        JOptionPane.ERROR_MESSAGE
                        );
                }

        tableContainer.removeAll();

        String keyword =
                searchField == null
                        ? ""
                        : searchField
                        .getText()
                        .trim()
                        .toLowerCase();

        String status =
                statusCombo == null
                        ? "Tất cả"
                        : String.valueOf(
                                statusCombo.getSelectedItem()
                        );

        for (TableInfo table : tables) {

            boolean matchSearch =
                    table.name
                            .toLowerCase()
                            .contains(keyword);

            boolean matchStatus = true;

            if (status.equals("Trống")) {

                matchStatus =
                        table.status.equals("TRỐNG");
            }

            if (status.equals("Đang phục vụ")) {

                matchStatus =
                        table.status.equals(
                                "ĐANG PHỤC VỤ"
                        );
            }

            if (
                    matchSearch &&
                    matchStatus
            ) {

                tableContainer.add(
                        createTableCard(table)
                );
            }
        }

        tableContainer.revalidate();

        tableContainer.repaint();
    }

    // =========================================================
    // TABLE CARD
    // =========================================================

    private JPanel createTableCard(
            TableInfo table
    ) {

        boolean empty =
                table.status.equals("TRỐNG");

        Color statusColor =
                empty
                        ? GREEN
                        : RED;

        Color statusBackground =
                empty
                        ? GREEN_LIGHT
                        : RED_LIGHT;

        JPanel card =
                new JPanel(
                        new BorderLayout()
                ) {

                    @Override
                    protected void paintComponent(
                            Graphics g
                    ) {

                        Graphics2D g2 =
                                (Graphics2D)
                                        g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2.setColor(
                                WHITE
                        );

                        g2.fillRoundRect(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                14,
                                14
                        );

                        g2.setColor(
                                statusColor
                        );

                        g2.setStroke(
                                new BasicStroke(
                                        2f
                                )
                        );

                        g2.drawRoundRect(
                                1,
                                1,
                                getWidth() - 2,
                                getHeight() - 2,
                                14,
                                14
                        );

                        g2.dispose();

                        super.paintComponent(g);
                    }
                };

        card.setOpaque(false);

        card.setPreferredSize(
                new Dimension(
                        290,
                        210
                )
        );

        card.setBorder(
                new EmptyBorder(
                        22,
                        22,
                        18,
                        22
                )
        );

        // =====================================================
        // TOP
        // =====================================================

        JPanel top =
                new JPanel(
                        new BorderLayout()
                );

        top.setOpaque(false);

        JLabel tableName =
                new JLabel(
                        table.name.toUpperCase()
                );

        tableName.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        19
                )
        );

        tableName.setForeground(TEXT);

        JLabel number =
                new JLabel(
                        "#" + String.format(
                                "%02d",
                                table.id
                        )
                );

        number.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        number.setForeground(
                TEXT_GRAY
        );

        top.add(
                tableName,
                BorderLayout.WEST
        );

        top.add(
                number,
                BorderLayout.EAST
        );

        card.add(
                top,
                BorderLayout.NORTH
        );

        // =====================================================
        // CENTER
        // =====================================================

        JPanel center =
                new JPanel();

        center.setOpaque(false);

        center.setLayout(
                new BoxLayout(
                        center,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel icon =
                new JLabel(
                        empty
                                ? "○"
                                : "●"
                );

        icon.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        icon.setForeground(
                statusColor
        );

        icon.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel statusLabel =
                new JLabel(
                        table.status
                );

        statusLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        statusLabel.setForeground(
                statusColor
        );

        statusLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel description =
                new JLabel(
                        empty
                                ? "Sẵn sàng phục vụ"
                                : "Đang có khách"
                );

        description.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        description.setForeground(
                TEXT_GRAY
        );

        description.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        center.add(
                Box.createVerticalStrut(10)
        );

        center.add(icon);

        center.add(
                Box.createVerticalStrut(4)
        );

        center.add(statusLabel);

        center.add(
                Box.createVerticalStrut(3)
        );

        center.add(description);

        card.add(
                center,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTON
        // =====================================================

        JButton detail = createTableActionButton("Chi tiết", PRIMARY);

        JButton edit = createTableActionButton("Sửa", PRIMARY);

        JButton delete = createTableActionButton("Xóa", RED);

        JButton statusButton = createTableActionButton(
                "Trạng thái",
                empty ? GREEN : RED
        );

        detail.addActionListener(
                e -> showTableDetail(table)
        );

        edit.addActionListener(
                e -> showTableDialog(table)
        );

        delete.addActionListener(
                e -> deleteTable(table)
        );

        statusButton.addActionListener(
                e -> chooseTableStatus(table)
        );

        JPanel bottom = new JPanel(new GridLayout(2, 2, 5, 5));

        bottom.setOpaque(false);

        bottom.add(statusButton);
        bottom.add(detail);
        bottom.add(edit);
        bottom.add(delete);

        card.add(
                bottom,
                BorderLayout.SOUTH
        );

        // =====================================================
        // HOVER
        // =====================================================

        card.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        card.setCursor(
                                Cursor.getPredefinedCursor(
                                        Cursor.HAND_CURSOR
                                )
                        );

                        card.setBorder(
                                new EmptyBorder(
                                        20,
                                        22,
                                        18,
                                        22
                                )
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        card.setBorder(
                                new EmptyBorder(
                                        22,
                                        22,
                                        18,
                                        22
                                )
                        );
                    }

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (
                                e.getClickCount() == 2
                        ) {

                            showTableDetail(table);
                        }
                    }
                }
        );

        return card;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    private JButton createPrimaryButton(
            String text
    ) {

        JButton button =
                new JButton(text) {

                    @Override
                    protected void paintComponent(
                            Graphics g
                    ) {

                        Graphics2D g2 =
                                (Graphics2D)
                                        g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        Color bg =
                                getModel().isRollover()
                                        ? PRIMARY_HOVER
                                        : PRIMARY;

                        g2.setColor(bg);

                        g2.fillRoundRect(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                10,
                                10
                        );

                        g2.dispose();

                        super.paintComponent(g);
                    }
                };

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        button.setForeground(Color.WHITE);

        button.setPreferredSize(
                new Dimension(
                        130,
                        44
                )
        );

        button.setContentAreaFilled(false);

        button.setBorderPainted(false);

        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

        private JButton createTableActionButton(String text, Color color) {
                JButton button = new JButton(text);
                button.setFont(new Font("Segoe UI", Font.BOLD, 11));
                button.setForeground(color);
                button.setBackground(color == RED
                                ? RED_LIGHT
                                : new Color(239, 246, 255));
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createEmptyBorder(6, 9, 6, 9));
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return button;
        }

    // =========================================================
    // ADD TABLE
    // =========================================================

        private void showAddTableDialog() {
                showTableDialog(null);
        }

        private void showTableDialog(TableInfo editingTable) {

        JTextField nameField =
                new JTextField();

                JTextField capacityField = new JTextField("4");

        nameField.setPreferredSize(
                new Dimension(
                        260,
                        35
                )
        );

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.add(
                new JLabel("Tên bàn:")
        );

        panel.add(
                Box.createVerticalStrut(8)
        );

        panel.add(nameField);

                panel.add(Box.createVerticalStrut(8));
                panel.add(new JLabel("Sức chứa:") );
                panel.add(Box.createVerticalStrut(8));
                panel.add(capacityField);

                if (editingTable != null) {
                        nameField.setText(editingTable.name);
                        capacityField.setText(String.valueOf(editingTable.capacity));
                }

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        editingTable == null ? "Thêm bàn mới" : "Sửa thông tin bàn",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (
                result ==
                        JOptionPane.OK_OPTION
        ) {

            String name =
                    nameField
                            .getText()
                            .trim();

            if (name.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập tên bàn!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

                        int capacity;
                        try {
                                capacity = Integer.parseInt(capacityField.getText().trim());
                                if (capacity <= 0) {
                                        throw new NumberFormatException();
                                }
                        } catch (NumberFormatException exception) {
                                JOptionPane.showMessageDialog(
                                                this,
                                                "Sức chứa phải là số nguyên lớn hơn 0!",
                                                "Thông báo",
                                                JOptionPane.WARNING_MESSAGE
                                );
                                return;
                        }

            try {
                                if (editingTable == null) {
                                        tableController.addTable(name, capacity);
                                } else {
                                        tableController.editTable(editingTable.id, name, capacity);
                                }
                loadTables();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể thêm bàn:\n" + exception.getMessage(),
                        "Lỗi cơ sở dữ liệu",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

        private void deleteTable(TableInfo table) {
                int result = JOptionPane.showConfirmDialog(
                                this,
                                "Bạn có chắc muốn xóa bàn \"" + table.name + "\"?",
                                "Xác nhận xóa",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                );

                if (result != JOptionPane.YES_OPTION) {
                        return;
                }

                try {
                        tableController.removeTable(table.id);
                        loadTables();
                } catch (Exception exception) {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Không thể xóa bàn. Có thể bàn đã có đơn hàng:\n"
                                                        + exception.getMessage(),
                                        "Lỗi cơ sở dữ liệu",
                                        JOptionPane.ERROR_MESSAGE
                        );
                }
        }

    // =========================================================
    // TABLE DETAIL
    // =========================================================

    private void showTableDetail(
            TableInfo table
    ) {

        String message =
                "Tên bàn: "
                        + table.name
                        + "\n\n"
                        + "Sức chứa: "
                        + table.capacity
                        + " người\n"
                        + "Trạng thái: "
                        + table.status;

        JOptionPane.showMessageDialog(
                this,
                message,
                "Thông tin bàn",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

        private void chooseTableStatus(TableInfo table) {
                String[] statuses = {"TRỐNG", "ĐANG PHỤC VỤ"};
                String selectedStatus = (String) JOptionPane.showInputDialog(
                                this,
                                "Chọn trạng thái cho " + table.name + ":",
                                "Cập nhật trạng thái bàn",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                statuses,
                                table.status
                );

                if (selectedStatus == null || selectedStatus.equals(table.status)) {
                        return;
                }

                changeTableStatus(table, "ĐANG PHỤC VỤ".equals(selectedStatus));
        }

        private void changeTableStatus(TableInfo table, boolean serving) {
                try {
                        tableController.setServing(table.id, serving);
                        table.status = serving ? "ĐANG PHỤC VỤ" : "TRỐNG";
                        loadTables();
                } catch (Exception exception) {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Không thể cập nhật trạng thái bàn:\n" + exception.getMessage(),
                                        "Lỗi cơ sở dữ liệu",
                                        JOptionPane.ERROR_MESSAGE
                        );
                }
        }

    // =========================================================
    // MODEL DEMO
    // =========================================================

    private static class TableInfo {

        int id;

        String name;

        String status;

        int capacity;

        TableInfo(
                int id,
                String name,
                int capacity,
                String status
        ) {

            this.id = id;

            this.name = name;

            this.capacity = capacity;

            this.status = status;
        }
    }
}