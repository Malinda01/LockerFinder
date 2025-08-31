package cw_final;

//package newpackage;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = MainFrame.AppFactory.createApp();
            frame.setVisible(true);
        });
    }
}
