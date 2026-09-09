package com.nhahang.view;

import com.nhahang.controller.ProductController;
import com.nhahang.dao.ProductDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductPanel extends JPanel {

    // =========================
    // COLORS
    // =========================

    private static final Color BACKGROUND = new Color(246, 248, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    private static final Color TEXT = new Color(25, 32, 45);
    private static final Color TEXT_GRAY = new Color(107, 118, 135);
    private static final Color BORDER = new Color(225, 229, 236);
    private static final Color GREEN = new Color(22, 163, 74);
    private static final Color RED = new Color(220, 38, 38);
    private static final Color ORANGE = new Color(234, 88, 12);

    // =========================
    // COMPONENTS
    // =========================

    private JPanel productGrid;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;

    // =========================
    // DATA
    // =========================

    private final List<Product> products = new ArrayList<>();
        private final ProductController productController = new ProductController();

    private final NumberFormat currency =
            NumberFormat.getInstance(new Locale("vi", "VN"));

    public ProductPanel() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        createUI();

        loadProducts();
        refreshProducts();
    }

    // ============================================================
    // MAIN UI
    // ============================================================

    private void createUI() {

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(28, 32, 28, 32));

        // =========================
        // HEADER
        // =========================

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản lý món ăn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Quản lý danh sách món ăn, giá bán và hình ảnh"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_GRAY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        JButton addButton = createPrimaryButton(
                "+  Thêm món"
        );

        addButton.addActionListener(e -> showProductDialog(null));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        // =========================
        // TOOLBAR
        // =========================

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(WHITE);
        toolbar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(15, 18, 15, 18)
                )
        );

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);

        JPanel searchIcon = createSearchIcon();
        searchIcon.setBorder(
                new EmptyBorder(0, 0, 0, 8)
        );

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(8, 12, 8, 12)
                )
        );
        searchField.setPreferredSize(new Dimension(280, 40));
        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm kiếm món ăn..."
        );
        searchField.setToolTipText("Tìm kiếm món ăn...");

        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    private void update() {
                        refreshProducts();
                    }

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        update();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        update();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        update();
                    }
                }
        );

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(
                FlowLayout.RIGHT,
                10,
                0
        ));
        filterPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Danh mục:");
        filterLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );
        filterLabel.setForeground(TEXT_GRAY);

        categoryCombo = new JComboBox<>(
                new String[]{
                        "Tất cả",
                        "Món chính",
                        "Món phụ",
                        "Khai vị",
                        "Đồ uống",
                        "Tráng miệng",
                        "Combo"
                }
        );

        categoryCombo.setPreferredSize(
                new Dimension(150, 40)
        );

        categoryCombo.addActionListener(
                e -> refreshProducts()
        );

        filterPanel.add(filterLabel);
        filterPanel.add(categoryCombo);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(filterPanel, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);

        center.add(toolbar, BorderLayout.NORTH);

        // =========================
        // PRODUCT GRID
        // =========================

        productGrid = new JPanel();

        productGrid.setOpaque(false);

        productGrid.setLayout(
                new GridLayout(0, 4, 18, 18)
        );

        JScrollPane scrollPane = new JScrollPane(productGrid);

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        center.add(scrollPane, BorderLayout.CENTER);

        main.add(center, BorderLayout.CENTER);

        add(main, BorderLayout.CENTER);
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

                int centerY = getHeight() / 2;
                int circleX = 6;
                int circleY = centerY - 8;
                g2.drawOval(circleX, circleY, 15, 15);
                g2.drawLine(circleX + 12, circleY + 12,
                        circleX + 20, circleY + 20);
                g2.dispose();
            }
        };
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(32, 40));
        return icon;
    }

    // ============================================================
    // REFRESH PRODUCT
    // ============================================================

    private void refreshProducts() {

        if (productGrid == null) {
            return;
        }

        productGrid.removeAll();

        String keyword =
                searchField == null
                        ? ""
                        : searchField.getText()
                        .trim()
                        .toLowerCase();

        String category =
                categoryCombo == null
                        ? "Tất cả"
                        : String.valueOf(
                                categoryCombo.getSelectedItem()
                        );

        int count = 0;

        for (Product product : products) {

            boolean matchName =
                    product.name
                            .toLowerCase()
                            .contains(keyword);

            boolean matchCategory =
                    category.equals("Tất cả")
                            || product.category.equals(category);

            if (matchName && matchCategory) {

                productGrid.add(
                        createProductCard(product)
                );

                count++;
            }
        }

        if (count == 0) {

            JPanel emptyPanel = new JPanel(
                    new GridBagLayout()
            );

            emptyPanel.setOpaque(false);

            JLabel empty = new JLabel(
                    "Không tìm thấy món ăn"
            );

            empty.setFont(
                    new Font("Segoe UI", Font.PLAIN, 16)
            );

            empty.setForeground(TEXT_GRAY);

            emptyPanel.add(empty);

            productGrid.setLayout(
                    new BorderLayout()
            );

            productGrid.add(
                    emptyPanel,
                    BorderLayout.CENTER
            );
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

        private void loadProducts() {
                products.clear();
                try {
                        for (ProductDAO.ProductRecord record : productController.loadProducts()) {
                                products.add(new Product(
                                                record.getId(),
                                                record.getName(),
                                                record.getCategory(),
                                                record.getPrice(),
                                                record.isAvailable(),
                                                record.getImagePath()
                                ));
                        }
                } catch (Exception exception) {
                        JOptionPane.showMessageDialog(
                                        this,
                                        "Không thể tải danh sách món ăn:\n" + exception.getMessage(),
                                        "Lỗi kết nối cơ sở dữ liệu",
                                        JOptionPane.ERROR_MESSAGE
                        );
                }
        }

    // ============================================================
    // PRODUCT CARD
    // ============================================================

    private JPanel createProductCard(Product product) {

        JPanel card = new JPanel(new BorderLayout());

        card.setBackground(WHITE);
        card.setBorder(
                new LineBorder(BORDER, 1, true)
        );

        // =========================
        // IMAGE
        // =========================

        JPanel imagePanel = createImagePanel(
                product.imagePath
        );

        imagePanel.setPreferredSize(
                new Dimension(0, 175)
        );

        card.add(
                imagePanel,
                BorderLayout.NORTH
        );

        // =========================
        // INFO
        // =========================

        JPanel info = new JPanel();
        info.setBackground(WHITE);
        info.setLayout(
                new BoxLayout(info, BoxLayout.Y_AXIS)
        );

        info.setBorder(
                new EmptyBorder(14, 15, 15, 15)
        );

        JLabel name = new JLabel(product.name);
        name.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );
        name.setForeground(TEXT);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel category = new JLabel(
                product.category
        );
        category.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );
        category.setForeground(TEXT_GRAY);
        category.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel priceStatus =
                new JPanel(new BorderLayout());

        priceStatus.setOpaque(false);
        priceStatus.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel price = new JLabel(
                currency.format(product.price) + " đ"
        );

        price.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );
        price.setForeground(PRIMARY);

        JLabel status = createStatusLabel(
                product.available
        );

        priceStatus.add(
                price,
                BorderLayout.WEST
        );

        priceStatus.add(
                status,
                BorderLayout.EAST
        );

        JPanel buttons = new JPanel(
                new GridLayout(1, 2, 8, 0)
        );

        buttons.setOpaque(false);
        buttons.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JButton editButton =
                createSmallButton("Sửa");

        JButton deleteButton =
                createDeleteButton("Xóa");

        editButton.addActionListener(
                e -> showProductDialog(product)
        );

        deleteButton.addActionListener(
                e -> deleteProduct(product)
        );

        buttons.add(editButton);
        buttons.add(deleteButton);

        info.add(name);
        info.add(Box.createVerticalStrut(4));
        info.add(category);
        info.add(Box.createVerticalStrut(12));
        info.add(priceStatus);
        info.add(Box.createVerticalStrut(14));
        info.add(buttons);

        card.add(
                info,
                BorderLayout.CENTER
        );

        // Hover
        card.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        card.setBorder(
                                new LineBorder(
                                        PRIMARY,
                                        1,
                                        true
                                )
                        );
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        card.setBorder(
                                new LineBorder(
                                        BORDER,
                                        1,
                                        true
                                )
                        );
                    }
                }
        );

        return card;
    }

    // ============================================================
    // IMAGE PANEL
    // ============================================================

    private JPanel createImagePanel(String imagePath) {

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(
                new Color(239, 242, 247)
        );

        if (imagePath != null
                && !imagePath.isBlank()) {

            File file = new File(imagePath);

            if (file.exists()) {

                ImageIcon icon =
                        new ImageIcon(imagePath);

                JLabel imageLabel =
                        new JLabel();

                imageLabel.setHorizontalAlignment(
                        SwingConstants.CENTER
                );

                imageLabel.setVerticalAlignment(
                        SwingConstants.CENTER
                );

                imageLabel.setIcon(
                        createScaledIcon(
                                icon,
                                350,
                                175
                        )
                );

                panel.add(
                        imageLabel,
                        BorderLayout.CENTER
                );

                return panel;
            }
        }

        // Không có ảnh
        JPanel noImage = new JPanel(
                new GridBagLayout()
        );

        noImage.setOpaque(false);

        JPanel icon = new JPanel() {

            @Override
            protected void paintComponent(
                    Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(
                        new Color(210, 216, 226)
                );

                g2.fillRoundRect(
                        0,
                        0,
                        60,
                        60,
                        15,
                        15
                );

                g2.setColor(
                        new Color(150, 158, 172)
                );

                g2.setStroke(
                        new BasicStroke(2)
                );

                g2.drawRoundRect(
                        15,
                        18,
                        30,
                        25,
                        4,
                        4
                );

                g2.drawOval(
                        20,
                        22,
                        7,
                        7
                );

                g2.drawLine(
                        18,
                        39,
                        27,
                        30
                );

                g2.drawLine(
                        27,
                        30,
                        33,
                        36
                );

                g2.drawLine(
                        33,
                        36,
                        42,
                        27
                );

                g2.dispose();
            }
        };

        icon.setPreferredSize(
                new Dimension(60, 60)
        );

        icon.setOpaque(false);

        noImage.add(icon);

        panel.add(
                noImage,
                BorderLayout.CENTER
        );

        return panel;
    }

    // ============================================================
    // IMAGE SCALE
    // ============================================================

    private ImageIcon createScaledIcon(
            ImageIcon original,
            int width,
            int height) {

        Image image =
                original.getImage();

        int originalWidth =
                original.getIconWidth();

        int originalHeight =
                original.getIconHeight();

        if (originalWidth <= 0
                || originalHeight <= 0) {

            return original;
        }

        double scaleX =
                (double) width / originalWidth;

        double scaleY =
                (double) height / originalHeight;

        double scale =
                Math.min(scaleX, scaleY);

        int newWidth =
                (int) (originalWidth * scale);

        int newHeight =
                (int) (originalHeight * scale);

        Image scaled =
                image.getScaledInstance(
                        newWidth,
                        newHeight,
                        Image.SCALE_SMOOTH
                );

        return new ImageIcon(scaled);
    }

    // ============================================================
    // ADD / EDIT DIALOG
    // ============================================================

    private void showProductDialog(Product editingProduct) {

        boolean editing =
                editingProduct != null;

        JDialog dialog =
                new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        editing
                                ? "Sửa món ăn"
                                : "Thêm món ăn",
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setSize(
                650,
                620
        );

        dialog.setLocationRelativeTo(this);

        JPanel main =
                new JPanel(new BorderLayout());

        main.setBackground(WHITE);

        main.setBorder(
                new EmptyBorder(25, 30, 25, 30)
        );

        // =========================
        // TITLE
        // =========================

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setOpaque(false);

        JLabel title =
                new JLabel(
                        editing
                                ? "Chỉnh sửa món ăn"
                                : "Thêm món ăn mới"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        title.setForeground(TEXT);

        header.add(
                title,
                BorderLayout.WEST
        );

        main.add(
                header,
                BorderLayout.NORTH
        );

        // =========================
        // FORM
        // =========================

        JPanel form = new JPanel();

        form.setOpaque(false);

        form.setLayout(
                new BoxLayout(
                        form,
                        BoxLayout.Y_AXIS
                )
        );

        form.setBorder(
                new EmptyBorder(
                        25,
                        0,
                        10,
                        0
                )
        );

        // Tên món
        JLabel nameLabel =
                createFieldLabel("Tên món ăn");

        JTextField nameField =
                new JTextField();

        nameField.setPreferredSize(
                new Dimension(
                        100,
                        42
                )
        );

        nameField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        nameField.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        // Danh mục
        JLabel categoryLabel =
                createFieldLabel("Danh mục");

        JComboBox<String> categoryField =
                new JComboBox<>(
                        new String[]{
                                "Món chính",
                                "Món phụ",
                                "Khai vị",
                                "Đồ uống",
                                "Tráng miệng",
                                "Combo"
                        }
                );

        categoryField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        // Giá
        JLabel priceLabel =
                createFieldLabel("Giá bán");

        JTextField priceField =
                new JTextField();

        priceField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        // Trạng thái
        JCheckBox availableCheck =
                new JCheckBox(
                        "Đang bán"
                );

        availableCheck.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        availableCheck.setOpaque(false);

        // =========================
        // IMAGE SELECT
        // =========================

        JLabel imageLabel =
                createFieldLabel(
                        "Hình ảnh món ăn"
                );

        JPanel imageChoosePanel =
                new JPanel(
                        new BorderLayout(12, 0)
                );

        imageChoosePanel.setOpaque(false);

        JLabel imagePreview =
                new JLabel();

        imagePreview.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        imagePreview.setVerticalAlignment(
                SwingConstants.CENTER
        );

        imagePreview.setPreferredSize(
                new Dimension(
                        140,
                        100
                )
        );

        imagePreview.setBorder(
                new LineBorder(
                        BORDER,
                        1,
                        true
                )
        );

        imagePreview.setOpaque(true);

        imagePreview.setBackground(
                new Color(245, 247, 250)
        );

        JButton chooseImageButton =
                createPrimaryButton(
                        "Chọn ảnh"
                );

        JLabel imagePathLabel =
                new JLabel(
                        "Chưa chọn ảnh"
                );

        imagePathLabel.setForeground(
                TEXT_GRAY
        );

        imagePathLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        JPanel imageRight =
                new JPanel();

        imageRight.setOpaque(false);

        imageRight.setLayout(
                new BoxLayout(
                        imageRight,
                        BoxLayout.Y_AXIS
                )
        );

        imageRight.add(
                chooseImageButton
        );

        imageRight.add(
                Box.createVerticalStrut(10)
        );

        imageRight.add(
                imagePathLabel
        );

        imageChoosePanel.add(
                imagePreview,
                BorderLayout.WEST
        );

        imageChoosePanel.add(
                imageRight,
                BorderLayout.CENTER
        );

        final String[] selectedImagePath =
                {null};

        // Nếu sửa
        if (editing) {

            nameField.setText(
                    editingProduct.name
            );

            categoryField.setSelectedItem(
                    editingProduct.category
            );

            priceField.setText(
                    String.valueOf(
                            editingProduct.price
                    )
            );

            availableCheck.setSelected(
                    editingProduct.available
            );

            selectedImagePath[0] =
                    editingProduct.imagePath;

            if (editingProduct.imagePath != null) {

                File file =
                        new File(
                                editingProduct.imagePath
                        );

                if (file.exists()) {

                    ImageIcon icon =
                            new ImageIcon(
                                    editingProduct.imagePath
                            );

                    imagePreview.setIcon(
                            createScaledIcon(
                                    icon,
                                    140,
                                    100
                            )
                    );

                    imagePathLabel.setText(
                            file.getName()
                    );
                }
            }
        }

        chooseImageButton.addActionListener(
                e -> {

                    JFileChooser chooser =
                            new JFileChooser();

                    chooser.setDialogTitle(
                            "Chọn ảnh món ăn"
                    );

                    chooser.setFileFilter(
                            new javax.swing.filechooser.FileNameExtensionFilter(
                                    "Ảnh món ăn (*.jpg, *.jpeg, *.png, *.gif)",
                                    "jpg",
                                    "jpeg",
                                    "png",
                                    "gif"
                            )
                    );

                    int result =
                            chooser.showOpenDialog(
                                    dialog
                            );

                    if (result ==
                            JFileChooser.APPROVE_OPTION) {

                        File selectedFile =
                                chooser.getSelectedFile();

                        try {

                            String savedPath =
                                    copyImageToProject(
                                            selectedFile
                                    );

                            selectedImagePath[0] =
                                    savedPath;

                            ImageIcon icon =
                                    new ImageIcon(
                                            savedPath
                                    );

                            imagePreview.setIcon(
                                    createScaledIcon(
                                            icon,
                                            140,
                                            100
                                    )
                            );

                            imagePathLabel.setText(
                                    selectedFile.getName()
                            );

                        } catch (IOException ex) {

                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "Không thể lưu ảnh:\n"
                                            + ex.getMessage(),
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
        );

        // Add form
        form.add(nameLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(nameField);

        form.add(Box.createVerticalStrut(15));

        form.add(categoryLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(categoryField);

        form.add(Box.createVerticalStrut(15));

        form.add(priceLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(priceField);

        form.add(Box.createVerticalStrut(15));

        form.add(imageLabel);
        form.add(Box.createVerticalStrut(7));
        form.add(imageChoosePanel);

        form.add(Box.createVerticalStrut(15));

        form.add(availableCheck);

        main.add(
                form,
                BorderLayout.CENTER
        );

        // =========================
        // BUTTONS
        // =========================

        JPanel buttons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        buttons.setOpaque(false);

        JButton cancelButton =
                createSmallButton("Hủy");

        JButton saveButton =
                createPrimaryButton(
                        editing
                                ? "Lưu thay đổi"
                                : "Thêm món"
                );

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        saveButton.addActionListener(
                e -> {

                    String name =
                            nameField
                                    .getText()
                                    .trim();

                    String priceText =
                            priceField
                                    .getText()
                                    .trim();

                    if (name.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Vui lòng nhập tên món!",
                                "Thông báo",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }

                    if (priceText.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Vui lòng nhập giá món!",
                                "Thông báo",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }

                    double price;

                    try {

                        price =
                                Double.parseDouble(
                                        priceText
                                                .replace(
                                                        ",",
                                                        ""
                                                )
                                                .replace(
                                                        ".",
                                                        ""
                                                )
                                );

                    } catch (NumberFormatException ex) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Giá món không hợp lệ!",
                                "Thông báo",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }

                    String selectedCategory =
                            String.valueOf(
                                    categoryField
                                            .getSelectedItem()
                            );

                    if (editing) {

                        editingProduct.name =
                                name;

                        editingProduct.category =
                                selectedCategory;

                        editingProduct.price =
                                price;

                        editingProduct.available =
                                availableCheck.isSelected();

                        editingProduct.imagePath =
                                selectedImagePath[0];

                        try {
                            productController.editProduct(new ProductDAO.ProductRecord(
                                    editingProduct.id,
                                    editingProduct.name,
                                    editingProduct.category,
                                    editingProduct.price,
                                    editingProduct.available,
                                    editingProduct.imagePath
                            ));
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "Không thể cập nhật món ăn:\n" + exception.getMessage(),
                                    "Lỗi cơ sở dữ liệu",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            loadProducts();
                            refreshProducts();
                            return;
                        }

                    } else {

                        try {
                            String id = productController.nextProductId();
                            productController.addProduct(new ProductDAO.ProductRecord(
                                    id,
                                    name,
                                    selectedCategory,
                                    price,
                                    availableCheck.isSelected(),
                                    selectedImagePath[0]
                            ));
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "Không thể thêm món ăn:\n" + exception.getMessage(),
                                    "Lỗi cơ sở dữ liệu",
                                    JOptionPane.ERROR_MESSAGE
                            );
                            return;
                        }
                    }

                    loadProducts();
                    refreshProducts();

                    dialog.dispose();
                }
        );

        buttons.add(cancelButton);
        buttons.add(saveButton);

        main.add(
                buttons,
                BorderLayout.SOUTH
        );

        dialog.setContentPane(main);
        dialog.setVisible(true);
    }

    // ============================================================
    // COPY IMAGE
    // ============================================================

    private String copyImageToProject(
            File source
    ) throws IOException {

        File imageFolder =
                new File(
                        "src/main/resources/images"
                );

        if (!imageFolder.exists()) {

            imageFolder.mkdirs();
        }

        String originalName =
                source.getName();

        String extension = "";

        int dot =
                originalName.lastIndexOf('.');

        if (dot >= 0) {

            extension =
                    originalName.substring(dot);
        }

        String fileName =
                "food_"
                        + System.currentTimeMillis()
                        + extension;

        File destination =
                new File(
                        imageFolder,
                        fileName
                );

        Files.copy(
                source.toPath(),
                destination.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );

        return destination.getAbsolutePath();
    }

    // ============================================================
    // DELETE
    // ============================================================

    private void deleteProduct(
            Product product
    ) {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa món \""
                                + product.name
                                + "\"?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (result ==
                JOptionPane.YES_OPTION) {

                        try {
                                productController.removeProduct(product.id);
                                loadProducts();
                        } catch (Exception exception) {
                                JOptionPane.showMessageDialog(
                                                this,
                                                "Không thể xóa món ăn:\n" + exception.getMessage(),
                                                "Lỗi cơ sở dữ liệu",
                                                JOptionPane.ERROR_MESSAGE
                                );
                                return;
                        }
            refreshProducts();
        }
    }

    // ============================================================
    // COMPONENT HELPERS
    // ============================================================

    private JLabel createFieldLabel(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(TEXT);

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    private JLabel createStatusLabel(
            boolean available
    ) {

        JLabel label =
                new JLabel(
                        available
                                ? "  Đang bán  "
                                : "  Ngừng bán  "
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        label.setForeground(
                available
                        ? GREEN
                        : RED
        );

        return label;
    }

    private JButton createPrimaryButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                new EmptyBorder(
                        10,
                        18,
                        10,
                        18
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e) {

                        button.setBackground(
                                PRIMARY_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e) {

                        button.setBackground(
                                PRIMARY
                        );
                    }
                }
        );

        return button;
    }

    private JButton createSmallButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        button.setForeground(TEXT);
        button.setBackground(
                new Color(245, 247, 250)
        );

        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    private JButton createDeleteButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        button.setForeground(RED);

        button.setBackground(
                new Color(254, 242, 242)
        );

        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // ============================================================
    // PRODUCT MODEL
    // ============================================================

    private static class Product {

        String id;
        String name;
        String category;
        double price;
        boolean available;
        String imagePath;

        Product(
                String id,
                String name,
                String category,
                double price,
                boolean available,
                String imagePath
        ) {

            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.available = available;
            this.imagePath = imagePath;
        }
    }
}