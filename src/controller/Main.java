package controller;

import javax.swing.*;
import util.UITheme;
import view.LoginFrame;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UITheme.apply();

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
