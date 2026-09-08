package com.nhahang.view;

import com.nhahang.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class HomePanel extends JPanel {

    private final User currentUser;

    private static final Color BACKGROUND =
            new Color(246, 248, 252);

    private static final Color TEXT =
            new Color(25, 32, 45);

    private static final Color TEXT_GRAY =
            new Color(107, 118, 135);

    private static final Color BORDER =
            new Color(225, 229, 236);

    private static final Color BLUE =
            new Color(37, 99, 235);

    private static final Color GREEN =
            new Color(34, 197, 94);

    private static final Color RED =
            new Color(239, 68, 68);

    public HomePanel(User user) {

        this.currentUser = user;

        initUI();
    }

    // ==========================================================
    // INIT
    // ==========================================================

    private void initUI() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );

        setBorder(
                new EmptyBorder(
                        32,
                        38,
                        32,
                        38
                )
        );

        // Scroll để giao diện không bị vỡ
        JScrollPane scroll =
                new JScrollPane(
                        createContent()
                );

        scroll.setBorder(null);

        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // ==========================================================
    // CONTENT
    // ==========================================================

    private JPanel createContent() {

        JPanel content =
                new JPanel();

        content.setBackground(
                BACKGROUND
        );

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        // =========================
        // HEADER
        // =========================

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setOpaque(false);

        header.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        75
                )
        );

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
                        "Trang chủ"
                );

        title.setForeground(
                TEXT
        );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
                )
        );

        JLabel subtitle =
                new JLabel(
                        "Tổng quan hoạt động nhà hàng hôm nay"
                );

        subtitle.setForeground(
                TEXT_GRAY
        );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        titlePanel.add(title);

        titlePanel.add(
                Box.createVerticalStrut(5)
        );

        titlePanel.add(subtitle);

        header.add(
                titlePanel,
                BorderLayout.WEST
        );

        // User bên phải

        JPanel welcome =
                new JPanel();

        welcome.setOpaque(false);

        welcome.setLayout(
                new BoxLayout(
                        welcome,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel hello =
                new JLabel(
                        "Xin chào,"
                );

        hello.setForeground(
                TEXT_GRAY
        );

        hello.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        JLabel username =
                new JLabel(
                        currentUser.getUsername()
                );

        username.setForeground(
                TEXT
        );

        username.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        hello.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        username.setAlignmentX(
                Component.RIGHT_ALIGNMENT
        );

        welcome.add(hello);

        welcome.add(
                Box.createVerticalStrut(2)
        );

        welcome.add(username);

        header.add(
                welcome,
                BorderLayout.EAST
        );

        content.add(header);

        content.add(
                Box.createVerticalStrut(25)
        );

        // =========================
        // STATISTICS
        // =========================

        JPanel stats =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                18,
                                0
                        )
                );

        stats.setOpaque(false);

        stats.add(
                createStatCard(
                        "10",
                        "Tổng số bàn",
                        BLUE,
                        "table"
                )
        );

        stats.add(
                createStatCard(
                        "7",
                        "Bàn đang trống",
                        GREEN,
                        "empty"
                )
        );

        stats.add(
                createStatCard(
                        "3",
                        "Đang phục vụ",
                        RED,
                        "service"
                )
        );

        stats.add(
                createStatCard(
                        "0",
                        "Chờ thanh toán",
                        new Color(
                                245,
                                158,
                                11
                        ),
                        "payment"
                )
        );

        stats.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        145
                )
        );

        content.add(stats);

        content.add(
                Box.createVerticalStrut(32)
        );

        // =========================
        // TABLE TITLE
        // =========================

        JLabel tableTitle =
                new JLabel(
                        "Trạng thái bàn"
                );

        tableTitle.setForeground(
                TEXT
        );

        tableTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        tableTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        content.add(tableTitle);

        content.add(
                Box.createVerticalStrut(15)
        );

        // =========================
        // TABLES
        // =========================

        JPanel tableGrid =
                new JPanel(
                        new GridLayout(
                                2,
                                5,
                                16,
                                16
                        )
                );

        tableGrid.setOpaque(false);

        for (
                int i = 1;
                i <= 10;
                i++
        ) {

            boolean serving =
                    i == 2 ||
                    i == 5 ||
                    i == 8;

            tableGrid.add(
                    createTableCard(
                            i,
                            serving
                    )
            );
        }

        tableGrid.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        250
                )
        );

        content.add(tableGrid);

        content.add(
                Box.createVerticalStrut(30)
        );

        // =========================
        // QUICK ACTION TITLE
        // =========================

        JLabel quickTitle =
                new JLabel(
                        "Thao tác nhanh"
                );

        quickTitle.setForeground(
                TEXT
        );

        quickTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        quickTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        content.add(quickTitle);

        content.add(
                Box.createVerticalStrut(15)
        );

        // =========================
        // QUICK ACTIONS
        // =========================

        JPanel quickPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                18,
                                0
                        )
                );

        quickPanel.setOpaque(false);

        quickPanel.add(
                createQuickButton(
                        "Gọi món",
                        "order"
                )
        );

        quickPanel.add(
                createQuickButton(
                        "Quản lý bàn",
                        "table"
                )
        );

        quickPanel.add(
                createQuickButton(
                        "Hóa đơn",
                        "invoice"
                )
        );

        quickPanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        110
                )
        );

        content.add(quickPanel);

        content.add(
                Box.createVerticalStrut(25)
        );

        // =========================
        // FOOTER
        // =========================

        JPanel footer =
                new JPanel(
                        new BorderLayout()
                );

        footer.setOpaque(false);

        JLabel status =
                new JLabel(
                        "●  Nhà hàng đang hoạt động"
                );

        status.setForeground(
                GREEN
        );

        status.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        JLabel summary =
                new JLabel(
                        "10 bàn  |  7 bàn trống  |  3 bàn đang phục vụ"
                );

        summary.setForeground(
                TEXT_GRAY
        );

        summary.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        footer.add(
                status,
                BorderLayout.WEST
        );

        footer.add(
                summary,
                BorderLayout.EAST
        );

        footer.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        35
                )
        );

        content.add(footer);

        return content;
    }

    // ==========================================================
    // STAT CARD
    // ==========================================================

    private JPanel createStatCard(
            String number,
            String text,
            Color color,
            String iconType
    ) {

        JPanel card =
                new RoundedPanel(
                        16,
                        Color.WHITE
                );

        card.setLayout(
                new BorderLayout()
        );

        card.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        JPanel icon =
                new JPanel() {

                    @Override
                    protected void paintComponent(
                            Graphics g
                    ) {

                        super.paintComponent(g);

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        g2.setColor(
                                new Color(
                                        color.getRed(),
                                        color.getGreen(),
                                        color.getBlue(),
                                        25
                                )
                        );

                        g2.fillRoundRect(
                                0,
                                0,
                                52,
                                52,
                                12,
                                12
                        );

                        g2.setColor(color);

                        g2.setStroke(
                                new BasicStroke(
                                        2f,
                                        BasicStroke.CAP_ROUND,
                                        BasicStroke.JOIN_ROUND
                                )
                        );

                        if (
                                iconType.equals("table")
                        ) {

                            g2.drawRoundRect(
                                    15,
                                    17,
                                    22,
                                    15,
                                    4,
                                    4
                            );

                            g2.drawLine(
                                    19,
                                    32,
                                    19,
                                    38
                            );

                            g2.drawLine(
                                    33,
                                    32,
                                    33,
                                    38
                            );

                        } else if (
                                iconType.equals("empty")
                        ) {

                            g2.drawOval(
                                    15,
                                    15,
                                    22,
                                    22
                            );

                            g2.drawLine(
                                    26,
                                    20,
                                    26,
                                    27
                            );

                            g2.drawLine(
                                    26,
                                    27,
                                    31,
                                    30
                            );

                        } else if (
                                iconType.equals("service")
                        ) {

                            g2.fillOval(
                                    21,
                                    14,
                                    10,
                                    10
                            );

                            g2.drawArc(
                                    14,
                                    25,
                                    24,
                                    15,
                                    0,
                                    180
                            );

                        } else {

                            g2.drawRect(
                                    15,
                                    14,
                                    22,
                                    25
                            );

                            g2.drawLine(
                                    19,
                                    21,
                                    33,
                                    21
                            );

                            g2.drawLine(
                                    19,
                                    27,
                                    33,
                                    27
                            );
                        }

                        g2.dispose();
                    }
                };

        icon.setOpaque(false);

        icon.setPreferredSize(
                new Dimension(
                        52,
                        52
                )
        );

        JPanel info =
                new JPanel();

        info.setOpaque(false);

        info.setLayout(
                new BoxLayout(
                        info,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel numberLabel =
                new JLabel(number);

        numberLabel.setForeground(
                TEXT
        );

        numberLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        JLabel textLabel =
                new JLabel(text);

        textLabel.setForeground(
                TEXT_GRAY
        );

        textLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        info.add(numberLabel);

        info.add(
                Box.createVerticalStrut(3)
        );

        info.add(textLabel);

        card.add(
                icon,
                BorderLayout.WEST
        );

        card.add(
                info,
                BorderLayout.CENTER
        );

        return card;
    }

    // ==========================================================
    // TABLE CARD
    // ==========================================================

    private JPanel createTableCard(
            int number,
            boolean serving
    ) {

        Color statusColor =
                serving
                        ? RED
                        : GREEN;

        String statusText =
                serving
                        ? "ĐANG PHỤC VỤ"
                        : "TRỐNG";

        JPanel card =
                new RoundedPanel(
                        14,
                        Color.WHITE
                );

        card.setLayout(
                new GridBagLayout()
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        statusColor.getRed(),
                                        statusColor.getGreen(),
                                        statusColor.getBlue(),
                                        150
                                ),
                                2
                        ),
                        new EmptyBorder(
                                12,
                                12,
                                12,
                                12
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;

        gbc.gridy = 0;

        gbc.anchor =
                GridBagConstraints.CENTER;

        JLabel table =
                new JLabel(
                        String.format(
                                "BÀN %02d",
                                number
                        )
                );

        table.setForeground(
                TEXT
        );

        table.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        card.add(
                table,
                gbc
        );

        gbc.gridy = 1;

        gbc.insets =
                new Insets(
                        7,
                        0,
                        0,
                        0
                );

        JLabel status =
                new JLabel(
                        statusText
                );

        status.setForeground(
                statusColor
        );

        status.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        card.add(
                status,
                gbc
        );

        return card;
    }

    // ==========================================================
    // QUICK BUTTON
    // ==========================================================

    private JButton createQuickButton(
            String text,
            String type
    ) {

        JButton button =
                new JButton();

        button.setLayout(
                new BorderLayout()
        );

        button.setBackground(
                Color.WHITE
        );

        button.setForeground(
                TEXT
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                15,
                                20,
                                15,
                                20
                        )
                )
        );

        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        JLabel icon =
                new JLabel();

        icon.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        icon.setPreferredSize(
                new Dimension(
                        35,
                        35
                )
        );

        icon.setIcon(
                new QuickIcon(type)
        );

        JLabel label =
                new JLabel(
                        text
                );

        label.setForeground(
                TEXT
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        button.add(
                icon,
                BorderLayout.WEST
        );

        button.add(
                label,
                BorderLayout.CENTER
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                new Color(
                                        248,
                                        250,
                                        255
                                )
                        );

                        button.setBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(
                                                BLUE,
                                                1
                                        ),
                                        new EmptyBorder(
                                                15,
                                                20,
                                                15,
                                                20
                                        )
                                )
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                Color.WHITE
                        );

                        button.setBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(
                                                BORDER
                                        ),
                                        new EmptyBorder(
                                                15,
                                                20,
                                                15,
                                                20
                                        )
                                )
                        );
                    }
                }
        );

        // =========================
        // ACTION
        // =========================

        button.addActionListener(
                e -> {

                    Window window =
                            SwingUtilities
                                    .getWindowAncestor(
                                            this
                                    );

                    if (
                            window instanceof MainFrame
                    ) {

                        MainFrame frame =
                                (MainFrame) window;

                        if (
                                type.equals("order")
                        ) {

                            frame.showPanel(
                                    new OrderPanel()
                            );

                        } else if (
                                type.equals("table")
                        ) {

                            frame.showPanel(
                                    new TablePanel()
                            );

                        } else if (
                                type.equals("invoice")
                        ) {

                            frame.showPanel(
                                    new InvoicePanel()
                            );
                        }
                    }
                }
        );

        return button;
    }

    // ==========================================================
    // ROUNDED PANEL
    // ==========================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;
        private final Color background;

        public RoundedPanel(
                int radius,
                Color background
        ) {

            this.radius = radius;

            this.background = background;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(background);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // ==========================================================
    // QUICK ICON
    // ==========================================================

    private static class QuickIcon
            implements Icon {

        private final String type;

        QuickIcon(String type) {

            this.type = type;
        }

        @Override
        public int getIconWidth() {

            return 24;
        }

        @Override
        public int getIconHeight() {

            return 24;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(BLUE);

            g2.setStroke(
                    new BasicStroke(
                            2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            if (
                    type.equals("table")
            ) {

                g2.drawRoundRect(
                        x + 3,
                        y + 5,
                        18,
                        12,
                        4,
                        4
                );

                g2.drawLine(
                        x + 7,
                        y + 17,
                        x + 7,
                        y + 21
                );

                g2.drawLine(
                        x + 17,
                        y + 17,
                        x + 17,
                        y + 21
                );

            } else if (
                    type.equals("order")
            ) {

                g2.drawRoundRect(
                        x + 3,
                        y + 3,
                        18,
                        17,
                        3,
                        3
                );

                g2.drawLine(
                        x + 7,
                        y + 8,
                        x + 17,
                        y + 8
                );

                g2.drawLine(
                        x + 7,
                        y + 12,
                        x + 17,
                        y + 12
                );

            } else {

                g2.drawRect(
                        x + 4,
                        y + 2,
                        16,
                        20
                );

                g2.drawLine(
                        x + 7,
                        y + 7,
                        x + 17,
                        y + 7
                );

                g2.drawLine(
                        x + 7,
                        y + 11,
                        x + 17,
                        y + 11
                );
            }

            g2.dispose();
        }
    }
}