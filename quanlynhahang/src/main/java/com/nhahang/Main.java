package com.nhahang;

import com.formdev.flatlaf.FlatLightLaf;
import com.nhahang.view.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Khởi động giao diện FlatLaf
        try {
            UIManager.setLookAndFeel(
                    new FlatLightLaf()
            );
        } catch (Exception e) {
            System.err.println(
                    "Không thể khởi tạo FlatLaf"
            );
            e.printStackTrace();
        }

        // Chạy giao diện trên EDT
        SwingUtilities.invokeLater(() -> {

            LoginFrame loginFrame =
                    new LoginFrame();

            loginFrame.setVisible(true);
        });
    }
}