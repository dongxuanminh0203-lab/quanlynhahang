package com.nhahang.view;

import com.nhahang.dao.UserDAO;
import com.nhahang.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private final UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        initUI();
    }

    private void initUI() {

        setTitle("Quản lý nhà hàng - Đăng nhập");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel(
                "QUẢN LÝ NHÀ HÀNG",
                SwingConstants.CENTER
        );

        lblTitle.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panel.add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;

        panel.add(
                new JLabel("Tài khoản:"),
                gbc
        );

        txtUsername = new JTextField(20);

        gbc.gridx = 1;

        panel.add(
                txtUsername,
                gbc
        );

        // Password
        gbc.gridy = 2;
        gbc.gridx = 0;

        panel.add(
                new JLabel("Mật khẩu:"),
                gbc
        );

        txtPassword = new JPasswordField(20);

        gbc.gridx = 1;

        panel.add(
                txtPassword,
                gbc
        );

        // Button
        btnLogin = new JButton("ĐĂNG NHẬP");

        btnLogin.setPreferredSize(
                new Dimension(150, 35)
        );

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        panel.add(
                btnLogin,
                gbc
        );

        add(panel);

        // Sự kiện đăng nhập
        btnLogin.addActionListener(e -> login());

        // Nhấn Enter trong ô mật khẩu
        txtPassword.addActionListener(e -> login());
    }

    private void login() {

        String username =
                txtUsername.getText().trim();

        String password =
                new String(txtPassword.getPassword());

        if (username.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập tài khoản!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập mật khẩu!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            txtPassword.requestFocus();
            return;
        }

        User user =
                userDAO.login(username, password);

        if (user != null) {

    JOptionPane.showMessageDialog(
            this,
            "Đăng nhập thành công!\n"
                    + "Xin chào: "
                    + user.getUsername(),
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE
    );

    dispose();

    MainFrame mainFrame =
            new MainFrame(user);

    mainFrame.setVisible(true);

} else {

    JOptionPane.showMessageDialog(
            this,
            "Tài khoản hoặc mật khẩu không đúng!",
            "Đăng nhập thất bại",
            JOptionPane.ERROR_MESSAGE
    );
}
    }
}