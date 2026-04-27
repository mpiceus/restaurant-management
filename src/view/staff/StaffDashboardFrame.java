package view.staff;

import util.Session;
import view.LoginFrame;
import view.panel.BanPanel;
import view.panel.HoaDonPanel;
import view.panel.LoaiMonAnPanel;
import view.panel.MonAnPanel;

import javax.swing.*;
import java.awt.*;

public class StaffDashboardFrame extends JFrame {

    public StaffDashboardFrame() {
        setTitle("Dashboard STAFF - Quản lý nhà hàng");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initMenu();
        initTabs();

        setVisible(true);
    }

    private void initMenu() {
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Tài khoản");

        String displayName = Session.getCurrentUser().getFullName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = Session.getCurrentUser().getUsername();
        }
        JMenuItem info = new JMenuItem("Đang đăng nhập: " + displayName);
        info.setEnabled(false);

        JMenuItem logout = new JMenuItem("Đăng xuất");
        logout.addActionListener(e -> {
            Session.clear();
            dispose();
            new LoginFrame();
        });

        menu.add(info);
        menu.addSeparator();
        menu.add(logout);
        bar.add(menu);
        setJMenuBar(bar);
    }

    private void initTabs() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Bàn", new BanPanel(false));
        tabs.addTab("Món ăn", new MonAnPanel(false));
        tabs.addTab("Loại món", new LoaiMonAnPanel(false));
        tabs.addTab("Hóa đơn", new HoaDonPanel(false));

        add(tabs, BorderLayout.CENTER);
    }
}
