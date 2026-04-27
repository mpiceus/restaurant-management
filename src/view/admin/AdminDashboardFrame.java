package view.admin;

import util.Session;
import view.LoginFrame;
import view.panel.*;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    public AdminDashboardFrame() {
        setTitle("Dashboard ADMIN - Quản lý nhà hàng");
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

        tabs.addTab("Món ăn", new MonAnPanel(true));
        tabs.addTab("Loại món", new LoaiMonAnPanel(true));
        tabs.addTab("Bảng giá", new BangGiaPanel());
        tabs.addTab("Bàn", new BanPanel(true));
        tabs.addTab("Hóa đơn", new HoaDonPanel(true));
        tabs.addTab("Nhân viên", new UserPanel());

        add(tabs, BorderLayout.CENTER);
    }
}
