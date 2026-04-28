package view.dialog;

import controller.BanController;
import controller.ChiTietBanController;
import controller.HoaDonController;
import controller.LoaiMonAnController;
import controller.MonAnController;
import model.ChiTietBanDTO;
import model.LoaiMonAn;
import model.MonAnWithPriceDTO;
import service.ServiceException;
import util.MoneyUtils;
import util.Session;
import util.UITheme;
import util.ImageUtils;
import view.common.InvoicePaperPanel;
import view.common.TableButtonEditor;
import view.common.TableButtonRenderer;
import view.common.WrapLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDialog extends JDialog {
    private final int banId;
    private final String tenBan;
    private final int userId;

    private final ChiTietBanController chiTietBanController = new ChiTietBanController();
    private final MonAnController monAnController = new MonAnController();
    private final LoaiMonAnController loaiMonAnController = new LoaiMonAnController();
    private final HoaDonController hoaDonController = new HoaDonController();
    private final BanController banController = new BanController();

    private Integer selectedLoaiId = null; // null = ALL
    private final JTextField txtSearch = new JTextField();

    private final JPanel leftCards = new JPanel(new CardLayout());
    private final JPanel rightCards = new JPanel(new CardLayout());

    private final JPanel dishGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));

    private final DefaultTableModel orderModel;
    private final JTable orderTable;
    private final JLabel lblTongTien = new JLabel("0");
    private final JButton btnThanhToan = new JButton("Thanh toan");

    // Checkout UI
    private final InvoicePaperPanel invoicePanel = new InvoicePaperPanel();
    private final CheckoutSummaryPanel checkoutSummaryPanel = new CheckoutSummaryPanel();

    public OrderDialog(Window parent, int banId, String tenBan) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.banId = banId;
        this.tenBan = tenBan;
        this.userId = Session.getCurrentUser().getUserId();

        setTitle("Order - " + tenBan);
        setSize(1200, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BEIGE);
        setLayout(new BorderLayout(8, 8));

        orderModel = new DefaultTableModel(new Object[]{
                "CTID", "MONID", "TEN", "DON_GIA", "SL", "THANH_TIEN", "+", "-", "HUY"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7 || column == 8;
            }
        };
        orderTable = new JTable(orderModel);

        // Hide CTID + MONID
        hideColumn(0);
        hideColumn(1);

        orderTable.getColumn("+").setCellRenderer(new TableButtonRenderer());
        orderTable.getColumn("+").setCellEditor(new TableButtonEditor("+", this::onPlusRow));
        orderTable.getColumn("-").setCellRenderer(new TableButtonRenderer());
        orderTable.getColumn("-").setCellEditor(new TableButtonEditor("-", this::onMinusRow));
        orderTable.getColumn("HUY").setCellRenderer(new TableButtonRenderer());
        orderTable.getColumn("HUY").setCellEditor(new TableButtonEditor("Huy", this::onHuyRow));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftSide(), buildRightSide());
        split.setDividerLocation(650);
        add(split, BorderLayout.CENTER);

        JButton btnClose = new JButton("Dong");
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BEIGE);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        loadLoaiButtons();
        refreshDishGrid();
        refreshOrder();
    }

    private Component buildLeftSide() {
        leftCards.setBackground(UITheme.BEIGE);
        leftCards.add(buildMenuPanel(), "MENU");
        leftCards.add(invoicePanel, "INVOICE");
        ((CardLayout) leftCards.getLayout()).show(leftCards, "MENU");
        return leftCards;
    }

    private Component buildRightSide() {
        rightCards.setBackground(UITheme.BEIGE);
        rightCards.add(buildOrderPanel(), "ORDER");
        rightCards.add(checkoutSummaryPanel, "CHECKOUT");
        ((CardLayout) rightCards.getLayout()).show(rightCards, "ORDER");
        return rightCards;
    }

    private JPanel buildMenuPanel() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(UITheme.BEIGE);
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBackground(UITheme.BEIGE);

        JPanel categories = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        categories.setBackground(UITheme.BEIGE);
        categories.setName("categories");
        top.add(categories, BorderLayout.NORTH);

        JPanel search = new JPanel(new BorderLayout(6, 6));
        search.setBackground(UITheme.BEIGE);
        txtSearch.addActionListener(e -> refreshDishGrid());
        JButton btnSearch = new JButton("Tim");
        btnSearch.addActionListener(e -> refreshDishGrid());
        search.add(txtSearch, BorderLayout.CENTER);
        search.add(btnSearch, BorderLayout.EAST);
        top.add(search, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);

        dishGrid.setBackground(UITheme.BEIGE);
        JScrollPane scroll = new JScrollPane(dishGrid);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        root.add(scroll, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildOrderPanel() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(UITheme.BEIGE);
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        root.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UITheme.BEIGE);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setBackground(UITheme.BEIGE);
        left.add(new JLabel("Tong tien hang: "));
        left.add(lblTongTien);
        left.add(new JLabel(" VND"));
        bottom.add(left, BorderLayout.WEST);

        btnThanhToan.addActionListener(e -> onThanhToan());
        bottom.add(btnThanhToan, BorderLayout.EAST);

        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private void loadLoaiButtons() {
        JPanel categories = findNamedPanel(leftCards, "categories");
        if (categories == null) {
            return;
        }
        categories.removeAll();

        JButton all = new JButton("ALL");
        all.addActionListener(e -> {
            selectedLoaiId = null;
            refreshDishGrid();
        });
        categories.add(all);

        List<LoaiMonAn> list = loaiMonAnController.getAll();
        for (LoaiMonAn l : list) {
            JButton b = new JButton(l.getTenLoai());
            b.addActionListener(e -> {
                selectedLoaiId = l.getLoaiId();
                refreshDishGrid();
            });
            categories.add(b);
        }

        categories.revalidate();
        categories.repaint();
    }

    private void refreshDishGrid() {
        String keyword = txtSearch.getText();
        List<MonAnWithPriceDTO> list = monAnController.search(keyword, selectedLoaiId, null);
        dishGrid.removeAll();
        for (MonAnWithPriceDTO m : list) {
            dishGrid.add(buildDishCard(m));
        }
        dishGrid.revalidate();
        dishGrid.repaint();
    }

    private JPanel buildDishCard(MonAnWithPriceDTO m) {
        boolean isHet = "HET".equalsIgnoreCase(String.valueOf(m.getTrangThai()));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
        card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.BEIGE);
        card.setPreferredSize(new Dimension(140, 190));

        JPanel img = new JPanel(new BorderLayout());
        img.setPreferredSize(new Dimension(120, 120));
        img.setMaximumSize(new Dimension(120, 120));
        img.setBackground(isHet ? new Color(0xDDDDDD) : Color.WHITE);
        img.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setOpaque(false);
        ImageIcon icon = tryLoadMonImage(m.getMonId(), 120);
        if (icon != null) {
            imgLabel.setIcon(icon);
            imgLabel.setText("");
        } else if (m.getTenMon() != null && (m.getTenMon().trim().length() > 0)) {
            imgLabel.setText(m.getTenMon());
        }
        img.add(imgLabel, BorderLayout.CENTER);

        JLabel ten = new JLabel(String.valueOf(m.getTenMon()).toUpperCase());
        JLabel gia = new JLabel(MoneyUtils.formatVnd(m.getGia()) + " VND");

        ten.setAlignmentX(Component.LEFT_ALIGNMENT);
        gia.setAlignmentX(Component.LEFT_ALIGNMENT);
        ten.setBorder(BorderFactory.createEmptyBorder(6, 8, 0, 8));
        gia.setBorder(BorderFactory.createEmptyBorder(2, 8, 8, 8));

        if (isHet) {
            ten.setForeground(Color.GRAY);
            gia.setForeground(Color.GRAY);
            imgLabel.setForeground(Color.GRAY);
        }

        card.add(Box.createVerticalStrut(8));
        img.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(img);
        card.add(ten);
        card.add(gia);

        if (!isHet) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    try {
                        chiTietBanController.addMon(banId, m.getMonId(), 1, userId);
                        try {
                            banController.updateTrangThai(banId, "DANG_PHUC_VU");
                        } catch (Exception ignored) {
                        }
                        refreshOrder();
                    } catch (ServiceException ex) {
                        JOptionPane.showMessageDialog(OrderDialog.this, ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(UITheme.BEIGE_2);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(UITheme.BEIGE);
                }
            });
        } else {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        return card;
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

    private void refreshOrder() {
        List<ChiTietBanDTO> items = chiTietBanController.getByBanAndUser(banId, userId);

        Map<Integer, MonAnWithPriceDTO> monById = new HashMap<>();
        for (MonAnWithPriceDTO m : monAnController.getAll()) {
            monById.put(m.getMonId(), m);
        }

        orderModel.setRowCount(0);
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietBanDTO it : items) {
            MonAnWithPriceDTO mon = monById.get(it.getMonId());
            BigDecimal donGia = mon == null ? BigDecimal.ZERO : (mon.getGia() == null ? BigDecimal.ZERO : mon.getGia());
            BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(it.getSoLuong()));
            tong = tong.add(thanhTien);
            orderModel.addRow(new Object[]{
                    it.getChiTietBanId(),
                    it.getMonId(),
                    it.getTenMon(),
                    MoneyUtils.formatVnd(donGia),
                    it.getSoLuong(),
                    MoneyUtils.formatVnd(thanhTien),
                    "+",
                    "-",
                    "Huy"
            });
        }

        lblTongTien.setText(MoneyUtils.formatVnd(tong));
        btnThanhToan.setEnabled(!items.isEmpty());

        checkoutSummaryPanel.setData(tenBan, items, monById);
        invoicePanel.setData(null, tenBan, Session.getCurrentUser().getFullName(), null, toInvoiceItems(items, monById));
    }

    private void onPlusRow(int row) {
        int chiTietBanId = (Integer) orderModel.getValueAt(row, 0);
        int qty = Integer.parseInt(String.valueOf(orderModel.getValueAt(row, 4)));
        try {
            chiTietBanController.updateSoLuong(chiTietBanId, qty + 1);
            refreshOrder();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onMinusRow(int row) {
        int chiTietBanId = (Integer) orderModel.getValueAt(row, 0);
        int qty = Integer.parseInt(String.valueOf(orderModel.getValueAt(row, 4)));
        int newQty = qty - 1;
        if (newQty <= 0) {
            onHuyRow(row);
            return;
        }
        try {
            chiTietBanController.updateSoLuong(chiTietBanId, newQty);
            refreshOrder();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onHuyRow(int row) {
        int chiTietBanId = (Integer) orderModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Huy mon nay?", "Xac nhan", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            chiTietBanController.delete(chiTietBanId);
            refreshOrder();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onThanhToan() {
        ((CardLayout) leftCards.getLayout()).show(leftCards, "INVOICE");
        ((CardLayout) rightCards.getLayout()).show(rightCards, "CHECKOUT");
        checkoutSummaryPanel.setOnHoanThanh(this::doHoanThanh);
    }

    private void doHoanThanh() {
        try {
            int hoaDonId = hoaDonController.checkout(banId, userId);
            try {
                banController.updateTrangThai(banId, "TRONG");
            } catch (Exception ignored) {
            }
            JOptionPane.showMessageDialog(this, "Da thanh toan ban " + tenBan + " (Hoa don " + hoaDonId + ")");
            dispose();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hideColumn(int idx) {
        orderTable.getColumnModel().getColumn(idx).setMinWidth(0);
        orderTable.getColumnModel().getColumn(idx).setMaxWidth(0);
        orderTable.getColumnModel().getColumn(idx).setWidth(0);
    }

    private JPanel findNamedPanel(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (c instanceof JPanel && name.equals(((JPanel) c).getName())) {
                return (JPanel) c;
            }
            if (c instanceof Container) {
                JPanel x = findNamedPanel((Container) c, name);
                if (x != null) {
                    return x;
                }
            }
        }
        return null;
    }

    private List<InvoicePaperPanel.LineItem> toInvoiceItems(List<ChiTietBanDTO> items, Map<Integer, MonAnWithPriceDTO> monById) {
        java.util.ArrayList<InvoicePaperPanel.LineItem> out = new java.util.ArrayList<>();
        for (ChiTietBanDTO it : items) {
            MonAnWithPriceDTO mon = monById.get(it.getMonId());
            BigDecimal donGia = mon == null ? BigDecimal.ZERO : (mon.getGia() == null ? BigDecimal.ZERO : mon.getGia());
            out.add(new InvoicePaperPanel.LineItem(it.getTenMon(), it.getSoLuong(), donGia));
        }
        return out;
    }

    private static class CheckoutSummaryPanel extends JPanel {
        private Runnable onHoanThanh;

        private final JTextArea summary = new JTextArea();
        private final JButton btnHoanThanh = new JButton("Hoan thanh");

        public CheckoutSummaryPanel() {
            setLayout(new BorderLayout(8, 8));
            setBackground(UITheme.BEIGE);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            summary.setEditable(false);
            summary.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            summary.setBackground(Color.WHITE);
            add(new JScrollPane(summary), BorderLayout.CENTER);

            btnHoanThanh.addActionListener(e -> {
                if (onHoanThanh != null) {
                    onHoanThanh.run();
                }
            });
            add(btnHoanThanh, BorderLayout.SOUTH);
        }

        public void setOnHoanThanh(Runnable r) {
            this.onHoanThanh = r;
        }

        public void setData(String tenBan, List<ChiTietBanDTO> items, Map<Integer, MonAnWithPriceDTO> monById) {
            BigDecimal tong = BigDecimal.ZERO;
            StringBuilder sb = new StringBuilder();
            sb.append("BAN: ").append(tenBan).append("\n\n");
            for (ChiTietBanDTO it : items) {
                MonAnWithPriceDTO mon = monById.get(it.getMonId());
                BigDecimal donGia = mon == null ? BigDecimal.ZERO : (mon.getGia() == null ? BigDecimal.ZERO : mon.getGia());
                BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(it.getSoLuong()));
                tong = tong.add(thanhTien);
                sb.append(it.getTenMon()).append(" x").append(it.getSoLuong()).append(" = ").append(MoneyUtils.formatVnd(thanhTien)).append("\n");
            }
            sb.append("\n");
            BigDecimal vat = tong.multiply(BigDecimal.valueOf(0.08));
            BigDecimal svc = tong.multiply(BigDecimal.valueOf(0.15));
            BigDecimal total = tong.add(vat).add(svc);
            sb.append("Tong thanh tien: ").append(MoneyUtils.formatVnd(tong)).append("\n");
            sb.append("VAT 8%: ").append(MoneyUtils.formatVnd(vat)).append("\n");
            sb.append("Phi dich vu 15%: ").append(MoneyUtils.formatVnd(svc)).append("\n");
            sb.append("Tong cong: ").append(MoneyUtils.formatVnd(total)).append("\n");
            summary.setText(sb.toString());
            summary.setCaretPosition(0);
        }
    }
}
