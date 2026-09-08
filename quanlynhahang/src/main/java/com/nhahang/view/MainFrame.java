package com.nhahang.view;

import com.nhahang.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {

    private final User currentUser;

    private JPanel contentPanel;

    private final List<JButton> menuButtons = new ArrayList<>();
    private JButton selectedButton;

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color SIDEBAR =
            new Color(17, 24, 39);

    private static final Color SIDEBAR_HOVER =
            new Color(31, 41, 55);

    private static final Color PRIMARY =
            new Color(37, 99, 235);

    private static final Color PRIMARY_HOVER =
            new Color(29, 78, 216);

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color TEXT =
            new Color(31, 41, 55);

    private static final Color TEXT_GRAY =
            new Color(107, 114, 128);

    private static final Color WHITE =
            Color.WHITE;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MainFrame(User user) {

        this.currentUser = user;

        initUI();
    }

    // =========================================================
    // INIT UI
    // =========================================================

    private void initUI() {

        setTitle("Quản lý nhà hàng");

        /*
         * Kích thước cửa sổ
         */
        setSize(1400, 850);

        setMinimumSize(
                new Dimension(1100, 700)
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLayout(
                new BorderLayout()
        );

        /*
         * SIDEBAR
         */
        JPanel sidebar = createSidebar();

        add(
                sidebar,
                BorderLayout.WEST
        );

        /*
         * CONTENT
         */
        contentPanel =
                new JPanel(
                        new BorderLayout()
                );

        contentPanel.setBackground(
                BACKGROUND
        );

        add(
                contentPanel,
                BorderLayout.CENTER
        );

        /*
         * Trang chủ mặc định
         */
        showPanel(
                new HomePanel(currentUser)
        );
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar() {

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        /*
         * QUAN TRỌNG:
         * Sidebar chỉ rộng 250px
         */
        sidebar.setPreferredSize(
                new Dimension(250, 0)
        );

        sidebar.setMinimumSize(
                new Dimension(250, 0)
        );

        sidebar.setMaximumSize(
                new Dimension(250, Integer.MAX_VALUE)
        );

        sidebar.setBackground(
                SIDEBAR
        );

        // =====================================================
        // TOP
        // =====================================================

        JPanel top =
                new JPanel();

        top.setOpaque(false);

        top.setLayout(
                new BoxLayout(
                        top,
                        BoxLayout.Y_AXIS
                )
        );

        top.setBorder(
                new EmptyBorder(
                        28,
                        18,
                        10,
                        18
                )
        );

        // =====================================================
        // LOGO
        // =====================================================

        JPanel logoPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        logoPanel.setOpaque(false);

        logoPanel.setPreferredSize(
                new Dimension(
                        214,
                        55
                )
        );

        logoPanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        55
                )
        );

        // Logo chữ R

        JPanel logo =
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
                                PRIMARY
                        );

                        g2.fillRoundRect(
                                0,
                                0,
                                48,
                                48,
                                10,
                                10
                        );

                        g2.setColor(
                                Color.WHITE
                        );

                        g2.setFont(
                                new Font(
                                        "Segoe UI",
                                        Font.BOLD,
                                        25
                                )
                        );

                        FontMetrics fm =
                                g2.getFontMetrics();

                        String text = "R";

                        int x =
                                (48 -
                                        fm.stringWidth(text))
                                        / 2;

                        int y =
                                (48 -
                                        fm.getHeight())
                                        / 2
                                        + fm.getAscent();

                        g2.drawString(
                                text,
                                x,
                                y
                        );

                        g2.dispose();
                    }
                };

        logo.setOpaque(false);

        logo.setPreferredSize(
                new Dimension(
                        48,
                        48
                )
        );

        // Tên nhà hàng

        JLabel logoText =
                new JLabel(
                        "NHÀ HÀNG"
                );

        logoText.setForeground(
                Color.WHITE
        );

        logoText.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        logoText.setBorder(
                new EmptyBorder(
                        0,
                        14,
                        0,
                        0
                )
        );

        logoPanel.add(logo);
        logoPanel.add(logoText);

        top.add(
                logoPanel
        );

        // Khoảng cách

        top.add(
                Box.createVerticalStrut(35)
        );

        // =====================================================
        // MENU TITLE
        // =====================================================

        JLabel menuTitle =
                new JLabel(
                        "MENU CHÍNH"
                );

        menuTitle.setForeground(
                new Color(
                        148,
                        163,
                        184
                )
        );

        menuTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        menuTitle.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        12,
                        0
                )
        );

        menuTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        top.add(
                menuTitle
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        JButton btnHome =
                createMenuButton(
                        "⌂",
                        "Trang chủ"
                );

        JButton btnTable =
                createMenuButton(
                        "▣",
                        "Quản lý bàn"
                );

        JButton btnProduct =
                createMenuButton(
                        "☷",
                        "Quản lý món ăn"
                );

        JButton btnOrder =
                createMenuButton(
                        "▤",
                        "Gọi món"
                );

        JButton btnInvoice =
                createMenuButton(
                        "▥",
                        "Hóa đơn"
                );

        JButton btnEmployee =
                createMenuButton(
                        "♙",
                        "Nhân viên"
                );

        JButton btnCustomer =
                createMenuButton(
                        "♙",
                        "Khách hàng"
                );

        JButton btnStatistics =
                createMenuButton(
                        "▥",
                        "Thống kê"
                );

        addMenu(
                top,
                btnHome
        );

        addMenu(
                top,
                btnTable
        );

        addMenu(
                top,
                btnProduct
        );

        addMenu(
                top,
                btnOrder
        );

        addMenu(
                top,
                btnInvoice
        );

        addMenu(
                top,
                btnEmployee
        );

        addMenu(
                top,
                btnCustomer
        );

        addMenu(
                top,
                btnStatistics
        );

        sidebar.add(
                top,
                BorderLayout.NORTH
        );

        // =====================================================
        // BOTTOM
        // =====================================================

        JPanel bottom =
                new JPanel();

        bottom.setOpaque(false);

        bottom.setLayout(
                new BoxLayout(
                        bottom,
                        BoxLayout.Y_AXIS
                )
        );

        bottom.setBorder(
                new EmptyBorder(
                        10,
                        18,
                        20,
                        18
                )
        );

        // =====================================================
        // USER
        // =====================================================

        JPanel userPanel =
                new JPanel(
                        new BorderLayout()
                );

        userPanel.setOpaque(false);

        userPanel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        // Chấm xanh

        JPanel status =
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
                                        34,
                                        197,
                                        94
                                )
                        );

                        g2.fillOval(
                                3,
                                15,
                                10,
                                10
                        );

                        g2.dispose();
                    }
                };

        status.setOpaque(false);

        status.setPreferredSize(
                new Dimension(
                        22,
                        40
                )
        );

        JLabel username =
                new JLabel(
                        currentUser.getUsername()
                );

        username.setForeground(
                Color.WHITE
        );

        username.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        userPanel.add(
                status,
                BorderLayout.WEST
        );

        userPanel.add(
                username,
                BorderLayout.CENTER
        );

        bottom.add(
                userPanel
        );

        bottom.add(
                Box.createVerticalStrut(8)
        );

        // =====================================================
        // LOGOUT
        // =====================================================

        JButton logout =
                createMenuButton(
                        "→",
                        "Đăng xuất"
                );

        bottom.add(
                logout
        );

        sidebar.add(
                bottom,
                BorderLayout.SOUTH
        );

        // =====================================================
        // EVENTS
        // =====================================================

        btnHome.addActionListener(
                e -> {

                    setSelectedButton(
                            btnHome
                    );

                    showPanel(
                            new HomePanel(
                                    currentUser
                            )
                    );
                }
        );

        btnTable.addActionListener(
                e -> {

                    setSelectedButton(
                            btnTable
                    );

                    showPanel(
                            new TablePanel()
                    );
                }
        );

        btnProduct.addActionListener(
                e -> {

                    setSelectedButton(
                            btnProduct
                    );

                    showPanel(
                            new ProductPanel()
                    );
                }
        );

        btnOrder.addActionListener(
                e -> {

                    setSelectedButton(
                            btnOrder
                    );

                    showPanel(
                            new OrderPanel()
                    );
                }
        );

        btnInvoice.addActionListener(
                e -> {

                    setSelectedButton(
                            btnInvoice
                    );

                    showPanel(
                            new InvoicePanel()
                    );
                }
        );

        btnEmployee.addActionListener(
                e -> {

                    setSelectedButton(
                            btnEmployee
                    );

                    showPanel(
                            new EmployeePanel()
                    );
                }
        );

        btnCustomer.addActionListener(
                e -> {

                    setSelectedButton(
                            btnCustomer
                    );

                    showPanel(
                            new CustomerPanel()
                    );
                }
        );

        btnStatistics.addActionListener(
                e -> {

                    setSelectedButton(
                            btnStatistics
                    );

                    showPanel(
                            new StatisticsPanel()
                    );
                }
        );

        logout.addActionListener(
                e -> logout()
        );

        // Trang chủ được chọn mặc định

        setSelectedButton(
                btnHome
        );

        return sidebar;
    }

    // =========================================================
    // ADD MENU
    // =========================================================

    private void addMenu(
            JPanel panel,
            JButton button
    ) {

        panel.add(button);

        panel.add(
                Box.createVerticalStrut(4)
        );

        menuButtons.add(
                button
        );
    }

    // =========================================================
    // CREATE MENU BUTTON
    // =========================================================

    private JButton createMenuButton(
            String iconText,
            String text
    ) {

        JButton button =
                new JButton();

        button.setLayout(
                new BorderLayout()
        );

        /*
         * Kích thước cố định
         */
        button.setPreferredSize(
                new Dimension(
                        214,
                        48
                )
        );

        button.setMinimumSize(
                new Dimension(
                        214,
                        48
                )
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        48
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        button.setBackground(
                SIDEBAR
        );

        button.setForeground(
                new Color(
                        226,
                        232,
                        240
                )
        );

        button.setBorder(
                new EmptyBorder(
                        0,
                        8,
                        0,
                        8
                )
        );

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setContentAreaFilled(true);

        button.setOpaque(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        // =====================================================
        // ICON
        // =====================================================

        JLabel icon =
                new JLabel(
                        iconText
                );

        icon.setForeground(
                new Color(
                        190,
                        203,
                        220
                )
        );

        icon.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.PLAIN,
                        20
                )
        );

        icon.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        icon.setPreferredSize(
                new Dimension(
                        38,
                        48
                )
        );

        // =====================================================
        // TEXT
        // =====================================================

        JLabel label =
                new JLabel(
                        text
                );

        label.setForeground(
                new Color(
                        226,
                        232,
                        240
                )
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        // =====================================================
        // ADD
        // =====================================================

        button.add(
                icon,
                BorderLayout.WEST
        );

        button.add(
                label,
                BorderLayout.CENTER
        );

        // =====================================================
        // HOVER
        // =====================================================

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        if (
                                button != selectedButton
                        ) {

                            button.setBackground(
                                    SIDEBAR_HOVER
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        if (
                                button != selectedButton
                        ) {

                            button.setBackground(
                                    SIDEBAR
                            );
                        }
                    }
                }
        );

        return button;
    }

    // =========================================================
    // SELECT BUTTON
    // =========================================================

    private void setSelectedButton(
            JButton button
    ) {

        for (
                JButton b :
                menuButtons
        ) {

            b.setBackground(
                    SIDEBAR
            );
        }

        selectedButton =
                button;

        selectedButton.setBackground(
                PRIMARY
        );
    }

    // =========================================================
    // SHOW PANEL
    // =========================================================

    public void showPanel(
            JPanel panel
    ) {

        contentPanel.removeAll();

        contentPanel.add(
                panel,
                BorderLayout.CENTER
        );

        contentPanel.revalidate();

        contentPanel.repaint();
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn đăng xuất?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                result ==
                        JOptionPane.YES_OPTION
        ) {

            dispose();

            LoginFrame login =
                    new LoginFrame();

            login.setLocationRelativeTo(null);

            login.setVisible(true);
        }
    }
}