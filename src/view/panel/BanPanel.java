package view.panel;

import controller.BanController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import model.Ban;
import service.ServiceException;
import util.RoundedButtonUI;
import util.RoundedPanel;
import util.ScrollUtils;
import util.Session;
import util.UITheme;
import view.common.WrapLayout;
import view.dialog.BanFormDialog;
import view.dialog.OrderDialog;

public class BanPanel extends JPanel {
    private final boolean adminMode;
    private final BanController banController = new BanController();
    private final controller.ChiTietBanController chiTietBanController = new controller.ChiTietBanController();
    private final controller.UserController userController = new controller.UserController();

    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 16, 16));
    private Integer selectedBanId = null;

    public BanPanel(boolean adminMode) {
        this.adminMode = adminMode;
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.BEIGE);

        grid.setBackground(UITheme.BEIGE);
        JScrollPane scroll = new JScrollPane(grid);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(scroll, BorderLayout.CENTER);

        add(buildBottom(), BorderLayout.SOUTH);

        ScrollUtils.apply(this);
        loadData();
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(UITheme.BEIGE);

        JButton btnRefresh = new JButton("Tải lại");
        btnRefresh.addActionListener(e -> loadData());
        p.add(btnRefresh);

        if (adminMode) {
            JButton btnAdd = new JButton("Thêm");
            btnAdd.setUI(new RoundedButtonUI());
            btnAdd.setBackground(UITheme.CARAMEL);
            btnAdd.setForeground(Color.WHITE);
            JButton btnEdit = new JButton("Sửa");
            btnEdit.setUI(new RoundedButtonUI());
            btnEdit.setBackground(UITheme.CARAMEL);
            btnEdit.setForeground(Color.WHITE);
            JButton btnDelete = new JButton("Xóa");
            btnDelete.setUI(new RoundedButtonUI());
            btnDelete.setBackground(UITheme.CARAMEL);
            btnDelete.setForeground(Color.WHITE);

            btnAdd.addActionListener(e -> onAdd());
            btnEdit.addActionListener(e -> onEdit());
            btnDelete.addActionListener(e -> onDelete());

            p.add(btnAdd);
            p.add(btnEdit);
            p.add(btnDelete);
        } /*else {
            JButton btnUpdateStatus = new JButton("Cập nhật trạng thái");
            btnUpdateStatus.addActionListener(e -> onUpdateStatus());
            p.add(btnUpdateStatus);
        }*/

        return p;
    }

    private void loadData() {
        List<Ban> list = banController.getAll();
        grid.removeAll();
        selectedBanId = null;

        for (Ban b : list) {
            grid.add(buildBanCard(b));
        }

        revalidate();
        repaint();
    }

    private JPanel buildBanCard(Ban b) {
        RoundedPanel card = new RoundedPanel(18);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(160, 160));
        card.setBackground(UITheme.BEIGE);
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        card.putClientProperty("banId", b.getBanId());

        JLabel name = new JLabel(String.valueOf(b.getTenBan()).toUpperCase(), SwingConstants.CENTER);
        name.setFont(name.getFont().deriveFont(Font.BOLD, 15f));
        name.setBorder(BorderFactory.createEmptyBorder(22, 8, 8, 8));

        String statusText = formatTrangThai(b.getTrangThai());
        JLabel status = new JLabel(statusText, SwingConstants.CENTER);
        status.setFont(status.getFont().deriveFont(Font.PLAIN, 12.5f));
        status.setBorder(BorderFactory.createEmptyBorder(0, 8, 20, 8));
        status.setForeground("TRONG".equalsIgnoreCase(b.getTrangThai()) ? new Color(0x2E7D32) : new Color(0xC62828));
        if ("DANG_PHUC_VU".equalsIgnoreCase(b.getTrangThai())) {
            Integer servingUserId = chiTietBanController.getServingUserId(b.getBanId());
            model.User servingUser = servingUserId == null ? null : userController.getById(servingUserId);
            if (servingUser != null) {
                String display = servingUser.getUsername();
                if (servingUser.getFullName() != null && !servingUser.getFullName().trim().isEmpty()) {
                    display += ". " + servingUser.getFullName().trim();
                }
                status.setText("<html>ĐANG PHỤC VỤ<br><b>" + display + "</b></html>");
            }
        }

        card.add(name, BorderLayout.CENTER);
        card.add(status, BorderLayout.SOUTH);

        Color normal = UITheme.BEIGE;
        Color hover = UITheme.BEIGE_2;
        Color selected = new Color(0xE2D6C0);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedBanId != null && selectedBanId.equals(b.getBanId())) {
                    return;
                }
                card.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (selectedBanId != null && selectedBanId.equals(b.getBanId())) {
                    return;
                }
                card.setBackground(normal);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectedBanId = b.getBanId();
                updateSelectedCardBackground(selectedBanId, selected, normal);

                if (!adminMode && Session.getCurrentUser() != null) {
                    OrderDialog dialog = new OrderDialog(SwingUtilities.getWindowAncestor(BanPanel.this), b.getBanId(), b.getTenBan());
                    dialog.setVisible(true);

                    // Sau khi order/checkout xong, reload de cap nhat trang thai
                    loadData();
                }
            }
        });

        return card;
    }

    private void updateSelectedCardBackground(int banId, Color selected, Color normal) {
        for (Component c : grid.getComponents()) {
            if (!(c instanceof JPanel)) {
                continue;
            }
            JPanel card = (JPanel) c;
            Object id = card.getClientProperty("banId");
            if (id instanceof Integer && id.equals(banId)) {
                card.setBackground(selected);
            } else {
                card.setBackground(normal);
            }
        }
    }

    private String formatTrangThai(String s) {
        if (s == null) {
            return "";
        }
        if ("TRONG".equalsIgnoreCase(s)) {
            return "TRỐNG";
        }
        if ("DANG_PHUC_VU".equalsIgnoreCase(s)) {
            return "ĐANG PHỤC VỤ";
        }
        return s;
    }

    private void onAdd() {
        BanFormDialog dialog = new BanFormDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            banController.create(dialog.getTenBan(), dialog.getTrangThai());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if (selectedBanId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chon 1 bàn để sửa.");
            return;
        }

        Ban b = banController.getById(selectedBanId);
        if (b == null) {
            JOptionPane.showMessageDialog(this, "Khong tim thay ban.");
            return;
        }

        BanFormDialog dialog = new BanFormDialog(SwingUtilities.getWindowAncestor(this),
                new BanFormDialog.InitialData(b.getBanId(), b.getTenBan(), b.getTrangThai()));
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            banController.update(b.getBanId(), dialog.getTenBan(), dialog.getTrangThai());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (selectedBanId == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon 1 ban de xoa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xoa ban id=" + selectedBanId + " ?", "Xac nhan",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            banController.delete(selectedBanId);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*private void onUpdateStatus() {
        if (selectedBanId == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon 1 ban.");
            return;
        }

        Ban b = banController.getById(selectedBanId);
        String current = b == null ? "TRONG" : b.getTrangThai();

        String[] options = new String[]{"TRONG", "DANG_PHUC_VU"};
        String newStatus = (String) JOptionPane.showInputDialog(this, "Chon trang thai:", "Cap nhat trang thai",
                JOptionPane.PLAIN_MESSAGE, null, options, current);
        if (newStatus == null) {
            return;
        }

        try {
            banController.updateTrangThai(selectedBanId, newStatus);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }*/
}
