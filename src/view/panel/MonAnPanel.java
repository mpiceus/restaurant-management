package view.panel;

import controller.LoaiMonAnController;
import controller.MonAnController;
import model.LoaiMonAn;
import model.MonAnWithPriceDTO;
import service.ServiceException;
import util.ImageUtils;
import util.MoneyUtils;
import util.UITheme;
import view.common.WrapLayout;
import view.dialog.MonAnFormDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonAnPanel extends JPanel {
    private final boolean editable; // ADMIN: true, STAFF: false

    private final MonAnController monAnController = new MonAnController();
    private final LoaiMonAnController loaiController = new LoaiMonAnController();

    private final JTextField txtKeyword = new JTextField();
    private final JTextField txtMonId = new JTextField();
    private final JComboBox<LoaiMonAn> cbLoai = new JComboBox<>();

    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
    private Integer selectedMonId = null;
    private final Map<Integer, MonAnWithPriceDTO> monById = new HashMap<>();

    public MonAnPanel(boolean editable) {
        this.editable = editable;
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.BEIGE);

        add(buildTop(), BorderLayout.NORTH);

        grid.setBackground(UITheme.BEIGE);
        JScrollPane scroll = new JScrollPane(grid);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(scroll, BorderLayout.CENTER);

        add(buildBottom(), BorderLayout.SOUTH);

        loadLoai();
        loadData(null, null, null);
    }

    private JPanel buildTop() {
        JPanel p = new JPanel(new GridLayout(2, 4, 8, 8));
        p.setBackground(UITheme.BEIGE);
        p.setBorder(BorderFactory.createTitledBorder("Tim kiem"));

        p.add(new JLabel("Tu khoa (ten):"));
        p.add(txtKeyword);

        p.add(new JLabel("Loai:"));
        p.add(cbLoai);

        p.add(new JLabel("Mon ID:"));
        p.add(txtMonId);

        JButton btnSearch = new JButton("Tim");
        JButton btnReset = new JButton("Reset");

        btnSearch.addActionListener(e -> onSearch());
        btnReset.addActionListener(e -> {
            txtKeyword.setText("");
            txtMonId.setText("");
            if (cbLoai.getItemCount() > 0) {
                cbLoai.setSelectedIndex(0);
            }
            loadData(null, null, null);
        });

        p.add(btnReset);
        p.add(btnSearch);
        return p;
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(UITheme.BEIGE);

        JButton btnRefresh = new JButton("Lam moi");
        btnRefresh.addActionListener(e -> loadData(null, null, null));
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

    private void loadLoai() {
        cbLoai.removeAllItems();
        cbLoai.addItem(new LoaiMonAn(0, "Tat ca"));
        List<LoaiMonAn> loaiList = loaiController.getAll();
        for (LoaiMonAn l : loaiList) {
            cbLoai.addItem(l);
        }

        cbLoai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LoaiMonAn) {
                    setText(((LoaiMonAn) value).getTenLoai());
                }
                return this;
            }
        });
    }

    private void loadData(String keyword, Integer loaiId, Integer monId) {
        List<MonAnWithPriceDTO> list = monAnController.search(keyword, loaiId, monId);
        monById.clear();
        grid.removeAll();
        selectedMonId = null;

        for (MonAnWithPriceDTO m : list) {
            monById.put(m.getMonId(), m);
            grid.add(buildMonCard(m));
        }

        revalidate();
        repaint();
    }

    private void onSearch() {
        String keyword = txtKeyword.getText();

        Integer monId = null;
        try {
            String s = txtMonId.getText().trim();
            if (!s.isEmpty()) {
                monId = Integer.parseInt(s);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Mon ID khong hop le.");
            return;
        }

        LoaiMonAn selected = (LoaiMonAn) cbLoai.getSelectedItem();
        Integer loaiId = (selected != null && selected.getLoaiId() != 0) ? selected.getLoaiId() : null;
        loadData(keyword, loaiId, monId);
    }

    private JPanel buildMonCard(MonAnWithPriceDTO m) {
        boolean isHet = "HET".equalsIgnoreCase(String.valueOf(m.getTrangThai()));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.BEIGE);
        card.setPreferredSize(new Dimension(210, 260));
        card.putClientProperty("monId", m.getMonId());

        JLabel title = new JLabel(String.valueOf(m.getTenMon()).toUpperCase());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13.5f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel img = new JPanel(new BorderLayout());
        img.setPreferredSize(new Dimension(160, 160));
        img.setMaximumSize(new Dimension(160, 160));
        img.setAlignmentX(Component.LEFT_ALIGNMENT);
        img.setBackground(isHet ? new Color(0xDDDDDD) : Color.WHITE);

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = tryLoadMonImage(m.getMonId(), 160);
        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setText(m.getTenMon());
        }
        if (isHet) {
            imgLabel.setForeground(Color.GRAY);
        }
        img.add(imgLabel, BorderLayout.CENTER);
        img.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel meta1 = new JLabel("ID: " + m.getMonId() + " | LOAI: " + (m.getTenLoai() == null ? "" : m.getTenLoai()));
        JLabel meta2 = new JLabel("GIA: " + MoneyUtils.formatVnd(m.getGia()) + " VND");
        JLabel meta3 = new JLabel("TRANG THAI: " + (m.getTrangThai() == null ? "" : m.getTrangThai()));

        meta1.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        meta2.setBorder(BorderFactory.createEmptyBorder(2, 10, 0, 10));
        meta3.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 10));
        meta1.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta2.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta3.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (isHet) {
            title.setForeground(Color.GRAY);
            meta1.setForeground(Color.GRAY);
            meta2.setForeground(Color.GRAY);
            meta3.setForeground(Color.GRAY);
        }

        card.add(title);
        card.add(img);
        card.add(meta1);
        card.add(meta2);
        card.add(meta3);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectedMonId = m.getMonId();
                highlightSelected();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (selectedMonId != null && selectedMonId.equals(m.getMonId())) {
                    return;
                }
                card.setBackground(isHet ? new Color(0xE6E6E6) : UITheme.BEIGE_2);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (selectedMonId != null && selectedMonId.equals(m.getMonId())) {
                    return;
                }
                card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.BEIGE);
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
            Object id = card.getClientProperty("monId");
            boolean isSelected = id instanceof Integer && id.equals(selectedMonId);
            MonAnWithPriceDTO m = (id instanceof Integer) ? monById.get((Integer) id) : null;
            boolean isHet = m != null && "HET".equalsIgnoreCase(String.valueOf(m.getTrangThai()));
            if (isSelected) {
                card.setBackground(isHet ? new Color(0xDADADA) : new Color(0xE2D6C0));
            } else {
                card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.BEIGE);
            }
        }
    }

    private ImageIcon tryLoadMonImage(int monId, int size) {
        String base = "assets/monan/" + monId;
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
        MonAnFormDialog dialog = new MonAnFormDialog(SwingUtilities.getWindowAncestor(this), loaiController, null);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            monAnController.create(dialog.getTenMon(), dialog.getLoaiId(), dialog.getTrangThai(), dialog.getGia());
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if (selectedMonId == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon 1 mon de sua.");
            return;
        }
        MonAnWithPriceDTO m = monById.get(selectedMonId);
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Khong tim thay mon.");
            return;
        }

        MonAnFormDialog dialog = new MonAnFormDialog(
                SwingUtilities.getWindowAncestor(this),
                loaiController,
                new MonAnFormDialog.InitialData(m.getMonId(), m.getTenMon(), m.getLoaiId(), m.getTrangThai())
        );
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            BigDecimal giaMoi = dialog.getGiaOptional();
            monAnController.update(m.getMonId(), dialog.getTenMon(), dialog.getLoaiId(), dialog.getTrangThai(), giaMoi);
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (selectedMonId == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon 1 mon de xoa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xoa mon id=" + selectedMonId + " ?", "Xac nhan",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            monAnController.delete(selectedMonId);
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

