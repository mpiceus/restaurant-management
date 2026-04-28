package view.panel;

import controller.LoaiMonAnController;
import model.LoaiMonAn;
import service.ServiceException;
import util.ImageUtils;
import util.UITheme;
import view.common.WrapLayout;
import view.dialog.LoaiMonAnFormDialog;
import view.dialog.MonAnByLoaiDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaiMonAnPanel extends JPanel {
    private final boolean editable; // ADMIN: true, STAFF: false
    private final LoaiMonAnController controller = new LoaiMonAnController();

    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
    private Integer selectedLoaiId = null;
    private final Map<Integer, LoaiMonAn> loaiById = new HashMap<>();

    public LoaiMonAnPanel(boolean editable) {
        this.editable = editable;
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.BEIGE);

        grid.setBackground(UITheme.BEIGE);
        JScrollPane scroll = new JScrollPane(grid);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(scroll, BorderLayout.CENTER);

        add(buildBottom(), BorderLayout.SOUTH);

        loadData();
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(UITheme.BEIGE);

        JButton btnRefresh = new JButton("Lam moi");
        btnRefresh.addActionListener(e -> loadData());
        p.add(btnRefresh);

        if (editable) {
            JButton btnAdd = new JButton("Them");
            JButton btnEdit = new JButton("Sua");
            JButton btnDelete = new JButton("Xoa");

            btnAdd.addActionListener(e -> onAdd());
            btnEdit.addActionListener(e -> onEdit());
            btnDelete.addActionListener(e -> onDelete());

            p.add(btnAdd);
            p.add(btnEdit);
            p.add(btnDelete);
        }

        return p;
    }

    private void loadData() {
        List<LoaiMonAn> list = controller.getAll();
        loaiById.clear();
        grid.removeAll();
        selectedLoaiId = null;

        for (LoaiMonAn l : list) {
            loaiById.put(l.getLoaiId(), l);
            grid.add(buildLoaiCard(l));
        }

        revalidate();
        repaint();
    }

    private JPanel buildLoaiCard(LoaiMonAn l) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        card.setBackground(UITheme.BEIGE);
        card.setPreferredSize(new Dimension(210, 240));
        card.putClientProperty("loaiId", l.getLoaiId());

        JLabel title = new JLabel(String.valueOf(l.getTenLoai()).toUpperCase());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel img = new JPanel(new BorderLayout());
        img.setPreferredSize(new Dimension(160, 160));
        img.setMaximumSize(new Dimension(160, 160));
        img.setAlignmentX(Component.LEFT_ALIGNMENT);
        img.setBackground(Color.WHITE);
        img.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = tryLoadLoaiImage(l.getLoaiId(), 160);
        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setText(l.getTenLoai());
        }
        img.add(imgLabel, BorderLayout.CENTER);

        JLabel hint = new JLabel("Click de xem mon");
        hint.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(img);
        card.add(hint);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedLoaiId = l.getLoaiId();
                highlightSelected();
                MonAnByLoaiDialog dialog = new MonAnByLoaiDialog(SwingUtilities.getWindowAncestor(LoaiMonAnPanel.this),
                        l.getLoaiId(), l.getTenLoai());
                dialog.setVisible(true);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedLoaiId != null && selectedLoaiId.equals(l.getLoaiId())) {
                    return;
                }
                card.setBackground(UITheme.BEIGE_2);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedLoaiId != null && selectedLoaiId.equals(l.getLoaiId())) {
                    return;
                }
                card.setBackground(UITheme.BEIGE);
            }
        });

        return card;
    }

    private void highlightSelected() {
        for (Component c : grid.getComponents()) {
            if (!(c instanceof JPanel)) {
                continue;
            }
            JPanel card = (JPanel) c;
            Object id = card.getClientProperty("loaiId");
            boolean isSelected = id instanceof Integer && id.equals(selectedLoaiId);
            card.setBackground(isSelected ? new Color(0xE2D6C0) : UITheme.BEIGE);
        }
    }

    private ImageIcon tryLoadLoaiImage(int loaiId, int size) {
        String base = "assets/loaimonan/" + loaiId;
        String[] exts = new String[]{".jpg", ".jpeg", ".png"};
        for (String ext : exts) {
            File f = new File(base + ext);
            if (f.exists()) {
                return ImageUtils.loadSquareIcon(f.getPath(), size);
            }
        }
        return null;
    }

    private void onAdd() {
        LoaiMonAnFormDialog dialog = new LoaiMonAnFormDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            controller.create(dialog.getTenLoai());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if (selectedLoaiId == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon 1 loai de sua.");
            return;
        }
        LoaiMonAn l = loaiById.get(selectedLoaiId);
        String tenLoai = l == null ? "" : l.getTenLoai();

        LoaiMonAnFormDialog dialog = new LoaiMonAnFormDialog(SwingUtilities.getWindowAncestor(this), tenLoai);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            controller.update(selectedLoaiId, dialog.getTenLoai());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (selectedLoaiId == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon 1 loai de xoa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xoa loai id=" + selectedLoaiId + " ?", "Xac nhan",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.delete(selectedLoaiId);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

