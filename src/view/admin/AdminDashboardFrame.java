package view.admin;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import util.RoundedButtonUI;
import util.Session;
import util.UITheme;
import view.LoginFrame;
import view.panel.*;

public class AdminDashboardFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final List<JButton> navButtons = new ArrayList<>();

    public AdminDashboardFrame() {
        setTitle("Dashboard ADMIN - Quản lý nhà hàng");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initLayout();

        setVisible(true);
    }

    private void initLayout() {
        getContentPane().setBackground(UITheme.BEIGE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);

        content.setBackground(UITheme.BEIGE);
        content.add(new BaoCaoThongKePanel(), "BAOCAO");
        content.add(new MonAnPanel(true), "MONAN");
        content.add(new LoaiMonAnPanel(true), "LOAI");
        content.add(new BangGiaPanel(), "BANGGIA");
        content.add(new BanPanel(true), "BAN");
        content.add(new HoaDonPanel(true), "HOADON");
        content.add(new UserPanel(), "NHANVIEN");

        add(content, BorderLayout.CENTER);

        cardLayout.show(content, "BAOCAO");
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setBackground(UITheme.BEIGE_2);
        side.setPreferredSize(new Dimension(210, 0));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(14, 12, 12, 12));

        JLabel brand = new JLabel("UMAMI BAM");
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 16f));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(brand);
        top.add(Box.createVerticalStrut(10));

        String displayName = Session.getCurrentUser().getFullName();
        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = Session.getCurrentUser().getUsername();
        }
        JLabel user = new JLabel("ADMIN: " + displayName);
        user.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(user);
        top.add(Box.createVerticalStrut(16));

        top.add(navButton("Báo cáo", "BAOCAO"));
        top.add(Box.createVerticalStrut(6));
        top.add(navButton("Món ăn", "MONAN"));
        top.add(Box.createVerticalStrut(6));
        top.add(navButton("Loại món", "LOAI"));
        top.add(Box.createVerticalStrut(6));
        top.add(navButton("Bảng giá", "BANGGIA"));
        top.add(Box.createVerticalStrut(6));
        top.add(navButton("Bàn", "BAN"));
        top.add(Box.createVerticalStrut(6));
        top.add(navButton("Hóa đơn", "HOADON"));
        top.add(Box.createVerticalStrut(6));
        top.add(navButton("Nhân viên", "NHANVIEN"));

        if (!navButtons.isEmpty()) {
            navButtons.get(0).setBackground(UITheme.CARAMEL);
        }

        side.add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));

        JButton logout = new JButton("Đăng xuất");
        logout.setUI(new RoundedButtonUI());
        logout.setBackground(UITheme.LATTE);
        logout.setForeground(Color.WHITE);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.addActionListener(e -> {
            Session.clear();
            dispose();
            new LoginFrame();
        });
        bottom.add(logout);

        side.add(bottom, BorderLayout.SOUTH);
        return side;
    }

    private JButton navButton(String label, String card) {

        JButton b = new JButton(label);

        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.setBackground(UITheme.SIDEBAR);
        b.setForeground(UITheme.SIDEBAR_TEXT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        //b.setMargin(new Insets(0, 14, 0, 0));
        b.setUI(new RoundedButtonUI());

        navButtons.add(b);

        b.addActionListener(e -> {

            cardLayout.show(content, card);

            // reset toàn bộ menu
            for (JButton btn : navButtons) {
                btn.setBackground(UITheme.SIDEBAR);
            }

            // tô màu menu đang chọn
            b.setBackground(UITheme.CARAMEL);

            content.repaint();
        });

        return b;
    }
}
