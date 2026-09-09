package com.nhahang.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ProductPanel extends JPanel {

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

    private static final Color RED =
            new Color(239, 68, 68);

    private static final Color ORANGE =
            new Color(245, 158, 11);

    private static final Color PURPLE =
            new Color(139, 92, 246);

    // =========================================================
    // COMPONENTS
    // =========================================================

    private JPanel productContainer;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;

    // =========================================================
    // DEMO DATA
    // =========================================================

    private final List<ProductInfo> products =
            new ArrayList<>();

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ProductPanel() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );

        createDemoData();

        initUI();

        loadProducts();
    }

    // =========================================================
    // DEMO DATA
    // =========================================================

    private void createDemoData() {

        products.add(
                new ProductInfo(
                        1,
                        "Phở bò",
                        "Món chính",
                        65000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        2,
                        "Cơm sườn",
                        "Món chính",
                        55000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        3,
                        "Mì xào bò",
                        "Món chính",
                        60000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        4,
                        "Gà chiên",
                        "Món chính",
                        85000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        5,
                        "Salad rau củ",
                        "Khai vị",
                        45000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        6,
                        "Khoai tây chiên",
                        "Khai vị",
                        35000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        7,
                        "Coca Cola",
                        "Đồ uống",
                        20000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        8,
                        "Pepsi",
                        "Đồ uống",
                        20000,
                        "Hết món"
                )
        );

        products.add(
                new ProductInfo(
                        9,
                        "Trà đào",
                        "Đồ uống",
                        35000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        10,
                        "Cà phê sữa",
                        "Đồ uống",
                        30000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        11,
                        "Bánh flan",
                        "Tráng miệng",
                        25000,
                        "Còn món"
                )
        );

        products.add(
                new ProductInfo(
                        12,
                        "Kem vani",
                        "Tráng miệng",
                        30000,
                        "Còn món"
                )
        );
    }

    // =========================================================
    // INIT UI
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
                        "Quản lý món ăn"
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
                        "Quản lý danh sách món ăn, giá bán và trạng thái phục vụ"
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

        JButton addButton =
                createPrimaryButton(
                        "+  Thêm món"
                );

        addButton.addActionListener(
                e -> showAddProductDialog()
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

        // SEARCH

        JPanel searchPanel =
                new JPanel(
                        new BorderLayout()
                );

        searchPanel.setOpaque(false);

        JLabel searchIcon =
                new JLabel("⌕");

        searchIcon.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        24
                )
        );

        searchIcon.setForeground(
                TEXT_GRAY
        );

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

        searchField.setPreferredSize(
                new Dimension(
                        300,
                        40
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
                "Tìm kiếm món ăn..."
        );

        searchField.addActionListener(
                e -> loadProducts()
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

        // CATEGORY

        JPanel filterPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        filterPanel.setOpaque(false);

        JLabel categoryLabel =
                new JLabel(
                        "Danh mục:"
                );

        categoryLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        categoryLabel.setForeground(
                TEXT_GRAY
        );

        categoryCombo =
                new JComboBox<>(
                        new String[]{
                                "Tất cả",
                                "Món chính",
                                "Khai vị",
                                "Đồ uống",
                                "Tráng miệng"
                        }
                );

        categoryCombo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        categoryCombo.setPreferredSize(
                new Dimension(
                        155,
                        40
                )
        );

        categoryCombo.addActionListener(
                e -> loadProducts()
        );

        filterPanel.add(
                categoryLabel
        );

        filterPanel.add(
                categoryCombo
        );

        toolbar.add(
                filterPanel,
                BorderLayout.EAST
        );

        center.add(
                toolbar,
                BorderLayout.NORTH
        );

        // =====================================================
        // PRODUCT CONTAINER
        // =====================================================

        productContainer =
                new JPanel();

        productContainer.setOpaque(false);

        productContainer.setLayout(
                new GridLayout(
                        0,
                        4,
                        18,
                        18
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        productContainer
                );

        scrollPane.setBorder(null);

        scrollPane.setOpaque(false);

        scrollPane.getViewport()
                .setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

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

    // =========================================================
    // LOAD PRODUCTS
    // =========================================================

    private void loadProducts() {

        productContainer.removeAll();

        String keyword =
                searchField == null
                        ? ""
                        : searchField
                        .getText()
                        .trim()
                        .toLowerCase();

        String category =
                categoryCombo == null
                        ? "Tất cả"
                        : String.valueOf(
                                categoryCombo
                                        .getSelectedItem()
                        );

        for (
                ProductInfo product :
                products
        ) {

            boolean matchSearch =
                    product.name
                            .toLowerCase()
                            .contains(keyword);

            boolean matchCategory =
                    category.equals("Tất cả")
                            ||
                    product.category.equals(category);

            if (
                    matchSearch
                            &&
                    matchCategory
            ) {

                productContainer.add(
                        createProductCard(
                                product
                        )
                );
            }
        }

        productContainer.revalidate();

        productContainer.repaint();
    }

    // =========================================================
    // PRODUCT CARD
    // =========================================================

    private JPanel createProductCard(
            ProductInfo product
    ) {

        boolean available =
                product.status.equals(
                        "Còn món"
                );

        Color statusColor =
                available
                        ? GREEN
                        : RED;

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
                                BORDER
                        );

                        g2.drawRoundRect(
                                0,
                                0,
                                getWidth() - 1,
                                getHeight() - 1,
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
                        250,
                        230
                )
        );

        card.setBorder(
                new EmptyBorder(
                        18,
                        18,
                        16,
                        18
                )
        );

        // =====================================================
        // FOOD ICON
        // =====================================================

        JPanel foodIcon =
                new JPanel() {

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

                        Color iconColor =
                                getCategoryColor(
                                        product.category
                                );

                        g2.setColor(
                                iconColor
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
                                Color.WHITE
                        );

                        g2.setFont(
                                new Font(
                                        "Segoe UI Emoji",
                                        Font.PLAIN,
                                        34
                                )
                        );

                        String emoji =
                                getCategoryEmoji(
                                        product.category
                                );

                        FontMetrics fm =
                                g2.getFontMetrics();

                        int x =
                                (getWidth()
                                        - fm.stringWidth(
                                                emoji
                                        ))
                                        / 2;

                        int y =
                                (getHeight()
                                        - fm.getHeight())
                                        / 2
                                        + fm.getAscent();

                        g2.drawString(
                                emoji,
                                x,
                                y
                        );

                        g2.dispose();
                    }
                };

        foodIcon.setPreferredSize(
                new Dimension(
                        70,
                        70
                )
        );

        foodIcon.setOpaque(false);

        JPanel iconWrapper =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        iconWrapper.setOpaque(false);

        iconWrapper.add(
                foodIcon
        );

        card.add(
                iconWrapper,
                BorderLayout.NORTH
        );

        // =====================================================
        // INFO
        // =====================================================

        JPanel info =
                new JPanel();

        info.setOpaque(false);

        info.setLayout(
                new BoxLayout(
                        info,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel name =
                new JLabel(
                        product.name
                );

        name.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        name.setForeground(
                TEXT
        );

        name.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel category =
                new JLabel(
                        product.category
                );

        category.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        category.setForeground(
                TEXT_GRAY
        );

        category.setBorder(
                new EmptyBorder(
                        3,
                        0,
                        7,
                        0
                )
        );

        category.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel price =
                new JLabel(
                        formatPrice(
                                product.price
                        )
                );

        price.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        price.setForeground(
                PRIMARY
        );

        price.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        info.add(
                Box.createVerticalStrut(
                        12
                )
        );

        info.add(name);

        info.add(category);

        info.add(price);

        card.add(
                info,
                BorderLayout.CENTER
        );

        // =====================================================
        // BOTTOM
        // =====================================================

        JPanel bottom =
                new JPanel(
                        new BorderLayout()
                );

        bottom.setOpaque(false);

        JLabel status =
                new JLabel(
                        "● " + product.status
                );

        status.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        status.setForeground(
                statusColor
        );

        bottom.add(
                status,
                BorderLayout.WEST
        );

        JButton edit =
                new JButton(
                        "Sửa"
                );

        edit.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        edit.setForeground(
                PRIMARY
        );

        edit.setBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        edit.setFocusPainted(false);

        edit.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        12,
                        6,
                        12
                )
        );

        edit.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        edit.addActionListener(
                e -> showEditProductDialog(
                        product
                )
        );

        bottom.add(
                edit,
                BorderLayout.EAST
        );

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
                    }

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (
                                e.getClickCount() == 2
                        ) {

                            showEditProductDialog(
                                    product
                            );
                        }
                    }
                }
        );

        return card;
    }

    // =========================================================
    // CATEGORY COLOR
    // =========================================================

    private Color getCategoryColor(
            String category
    ) {

        switch (category) {

            case "Món chính":
                return PRIMARY;

            case "Khai vị":
                return ORANGE;

            case "Đồ uống":
                return new Color(
                        14,
                        165,
                        233
                );

            case "Tráng miệng":
                return PURPLE;

            default:
                return PRIMARY;
        }
    }

    // =========================================================
    // CATEGORY EMOJI
    // =========================================================

    private String getCategoryEmoji(
            String category
    ) {

        switch (category) {

            case "Món chính":
                return "🍜";

            case "Khai vị":
                return "🥗";

            case "Đồ uống":
                return "🥤";

            case "Tráng miệng":
                return "🍰";

            default:
                return "🍴";
        }
    }

    // =========================================================
    // FORMAT PRICE
    // =========================================================

    private String formatPrice(
            double price
    ) {

        return String.format(
                "%,.0f đ",
                price
        );
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

        button.setForeground(
                Color.WHITE
        );

        button.setPreferredSize(
                new Dimension(
                        130,
                        44
                )
        );

        button.setContentAreaFilled(
                false
        );

        button.setBorderPainted(
                false
        );

        button.setFocusPainted(
                false
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // =========================================================
    // ADD PRODUCT
    // =========================================================

    private void showAddProductDialog() {

        JTextField nameField =
                new JTextField();

        JTextField priceField =
                new JTextField();

        JComboBox<String> categoryField =
                new JComboBox<>(
                        new String[]{
                                "Món chính",
                                "Khai vị",
                                "Đồ uống",
                                "Tráng miệng"
                        }
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
                new JLabel("Tên món:")
        );

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(nameField);

        panel.add(
                Box.createVerticalStrut(10)
        );

        panel.add(
                new JLabel("Danh mục:")
        );

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(categoryField);

        panel.add(
                Box.createVerticalStrut(10)
        );

        panel.add(
                new JLabel("Giá:")
        );

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(priceField);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Thêm món ăn",
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

            String priceText =
                    priceField
                            .getText()
                            .trim();

            if (
                    name.isEmpty()
                            ||
                    priceText.isEmpty()
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Vui lòng nhập đầy đủ thông tin!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            try {

                double price =
                        Double.parseDouble(
                                priceText
                        );

                products.add(
                        new ProductInfo(
                                products.size() + 1,
                                name,
                                String.valueOf(
                                        categoryField
                                                .getSelectedItem()
                                ),
                                price,
                                "Còn món"
                        )
                );

                loadProducts();

            } catch (
                    NumberFormatException ex
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Giá món phải là số!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // =========================================================
    // EDIT PRODUCT
    // =========================================================

    private void showEditProductDialog(
            ProductInfo product
    ) {

        JTextField nameField =
                new JTextField(
                        product.name
                );

        JTextField priceField =
                new JTextField(
                        String.valueOf(
                                product.price
                        )
                );

        JComboBox<String> categoryField =
                new JComboBox<>(
                        new String[]{
                                "Món chính",
                                "Khai vị",
                                "Đồ uống",
                                "Tráng miệng"
                        }
                );

        categoryField.setSelectedItem(
                product.category
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
                new JLabel("Tên món:")
        );

        panel.add(
                nameField
        );

        panel.add(
                Box.createVerticalStrut(10)
        );

        panel.add(
                new JLabel("Danh mục:")
        );

        panel.add(
                categoryField
        );

        panel.add(
                Box.createVerticalStrut(10)
        );

        panel.add(
                new JLabel("Giá:")
        );

        panel.add(
                priceField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Sửa món ăn",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (
                result ==
                        JOptionPane.OK_OPTION
        ) {

            try {

                product.name =
                        nameField
                                .getText()
                                .trim();

                product.price =
                        Double.parseDouble(
                                priceField
                                        .getText()
                                        .trim()
                        );

                product.category =
                        String.valueOf(
                                categoryField
                                        .getSelectedItem()
                        );

                loadProducts();

            } catch (
                    NumberFormatException ex
            ) {

                JOptionPane.showMessageDialog(
                        this,
                        "Giá món không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // =========================================================
    // MODEL
    // =========================================================

    private static class ProductInfo {

        int id;

        String name;

        String category;

        double price;

        String status;

        ProductInfo(
                int id,
                String name,
                String category,
                double price,
                String status
        ) {

            this.id = id;

            this.name = name;

            this.category = category;

            this.price = price;

            this.status = status;
        }
    }
}