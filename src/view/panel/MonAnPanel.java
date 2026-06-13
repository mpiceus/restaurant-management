package view.panel;

import controller.LoaiMonAnController;
import controller.MonAnController;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.LoaiMonAn;
import model.MonAnWithPriceDTO;
import service.ServiceException;
import util.ImageUtils;
import util.MoneyUtils;
import util.RoundedButtonUI;
import util.RoundedPanel;
import util.ScrollUtils;
import util.UITheme;
import view.common.WrapLayout;
import view.dialog.MonAnFormDialog;

public class MonAnPanel extends JPanel {
    private final boolean editable; // ADMIN: true, STAFF: false

    private final MonAnController monAnController = new MonAnController();
    private final LoaiMonAnController loaiController = new LoaiMonAnController();

    private final JTextField txtKeyword = new JTextField();
    private final JTextField txtMonId = new JTextField();
    private final JComboBox<LoaiMonAn> cbLoai = new JComboBox<>();

    private final JPanel grid = new JPanel(new WrapLayout(FlowLayout.CENTER, 12, 12));
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
        ScrollUtils.apply(this);
        loadData(null, null, null);
    }

    private JPanel buildTop() {
        JPanel p = new RoundedPanel(18);
        p.setLayout(new GridLayout(2, 4, 8, 8));
        p.setBackground(UITheme.BEIGE);
        p.setBorder(BorderFactory.createEmptyBorder(20, 12, 12, 12));
        JLabel lblTitle = new JLabel("Tìm kiếm");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));

        p.add(new JLabel("Từ khóa (tên):"));
        p.add(txtKeyword);

        p.add(new JLabel("Loại:"));
        p.add(cbLoai);

        p.add(new JLabel("Món ID:"));
        p.add(txtMonId);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setUI(new RoundedButtonUI());
        btnSearch.setBackground(UITheme.CARAMEL);
        btnSearch.setForeground(Color.WHITE);

        JButton btnReset = new JButton("Reset");
        btnReset.setUI(new RoundedButtonUI());
        btnReset.setBackground(UITheme.LATTE);
        btnReset.setForeground(Color.WHITE);

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

        JButton btnRefresh = new JButton("Reset");
        btnRefresh.setUI(new RoundedButtonUI());
        btnRefresh.setBackground(UITheme.LATTE);
        btnRefresh.setForeground(Color.WHITE);

        btnRefresh.addActionListener(e -> loadData(null, null, null));
        p.add(btnRefresh);

        if (editable) {
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
        }
        return p;
    }

    private void loadLoai() {
        cbLoai.removeAllItems();
        cbLoai.addItem(new LoaiMonAn(0, "Tất cả"));
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

        RoundedPanel card = new RoundedPanel(18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        //card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        card.setBorder(null);
        card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.SAND);
        card.setPreferredSize(new Dimension(210, 260));
        card.putClientProperty("monId", m.getMonId());

        JLabel title = new JLabel(String.valueOf(m.getTenMon()).toUpperCase());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13.5f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel img = new JPanel(new BorderLayout());
        img.setPreferredSize(new Dimension(160, 160));
        img.setMaximumSize(new Dimension(160, 160));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);
        //img.setBackground(isHet ? new Color(0xDDDDDD) : Color.WHITE);
        img.setOpaque(false);

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(160, 160));
        imgLabel.setMinimumSize(new Dimension(160, 160));
        imgLabel.setMaximumSize(new Dimension(160, 160));
        imgLabel.setOpaque(false);

        ImageIcon icon = tryLoadMonImage(m.getHinhAnh(), 160);
        if (icon != null) {
            imgLabel.setIcon(icon);
        } else {
            imgLabel.setText(m.getTenMon());
        }
        if (isHet) {
            imgLabel.setForeground(Color.GRAY);
        }
        img.add(imgLabel, BorderLayout.CENTER);
        img.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        img.setMaximumSize(new Dimension(180, 160));

        JLabel meta1 = new JLabel("ID: " + m.getMonId() + " | Loại: " + (m.getTenLoai() == null ? "" : m.getTenLoai()));
        JLabel meta2 = new JLabel("Giá: " + MoneyUtils.formatVnd(m.getGia()) + " VND");
        JLabel meta3 = new JLabel("Trạng thái: " + (m.getTrangThai() == null ? "" : m.getTrangThai()));

        meta1.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        meta2.setBorder(BorderFactory.createEmptyBorder(2, 10, 0, 10));
        meta3.setBorder(BorderFactory.createEmptyBorder(2, 10, 10, 10));
        meta1.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta2.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta3.setAlignmentX(Component.CENTER_ALIGNMENT);

        meta1.setHorizontalAlignment(SwingConstants.CENTER);
        meta2.setHorizontalAlignment(SwingConstants.CENTER);
        meta3.setHorizontalAlignment(SwingConstants.CENTER);

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
                card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.SAND);
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

            boolean isSelected = id instanceof Integer 
                    && id.equals(selectedMonId);

            MonAnWithPriceDTO m = (id instanceof Integer)
                    ? monById.get((Integer) id)
                    : null;

            boolean isHet = m != null 
                    && "HET".equalsIgnoreCase(String.valueOf(m.getTrangThai()));

            if (isSelected) {
                // Được chọn
                card.setBackground(isHet 
                        ? new Color(0xDADADA) 
                        : UITheme.CARAMEL);

            } else {
                // Bình thường
                card.setBackground(isHet 
                        ? new Color(0xEEEEEE) 
                        : UITheme.SAND);
            }
        }
    }
    private ImageIcon tryLoadMonImage(String imagePath, int size) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        File f = new File(imagePath);

        if (!f.exists()) {
            return null;
        }

        return ImageUtils.loadSquareIcon(imagePath, size);
    }

    private void onAdd() {
        MonAnFormDialog dialog = new MonAnFormDialog(SwingUtilities.getWindowAncestor(this), loaiController, null);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            monAnController.create(dialog.getTenMon(), dialog.getLoaiId(), dialog.getTrangThai(), dialog.getGia(), dialog.getHinhAnh());
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if (selectedMonId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 món để sửa.");
            return;
        }
        MonAnWithPriceDTO m = monById.get(selectedMonId);
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy món.");
            return;
        }

        MonAnFormDialog dialog = new MonAnFormDialog(
                SwingUtilities.getWindowAncestor(this),
                loaiController,
                new MonAnFormDialog.InitialData(
                    m.getMonId(), m.getTenMon(), m.getLoaiId(), m.getTrangThai(), m.getGia(),m.getHinhAnh())
        );
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            BigDecimal giaMoi = dialog.getGiaOptional();
            monAnController.update(
                    m.getMonId(),
                    dialog.getTenMon(),
                    dialog.getLoaiId(),
                    dialog.getTrangThai(),
                    giaMoi,
                    dialog.getHinhAnh()
            );
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (selectedMonId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 món để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa món id=" + selectedMonId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            monAnController.delete(selectedMonId);
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

