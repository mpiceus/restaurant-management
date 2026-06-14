package view.dialog;

import controller.BanController;
import controller.ChiTietBanController;
import controller.HoaDonController;
import controller.LoaiMonAnController;
import controller.MonAnController;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import model.ChiTietBanDTO;
import model.LoaiMonAn;
import model.MonAnWithPriceDTO;
import service.ServiceException;
import util.ImageUtils;
import util.MoneyUtils;
import util.PDFInvoiceUtil;
import util.RoundedButtonUI;
import util.RoundedPanel;
import util.ScrollUtils;
import util.Session;
import util.UITheme;
import view.common.InvoicePaperPanel;
import view.common.TableButtonEditor;
import view.common.TableButtonRenderer;
import view.common.WrapLayout;

public class OrderDialog extends JDialog {
    private final int banId;
    private final String tenBan;
    private final int userId;
    private Integer currentHoaDonId = null;
    private boolean daThanhToan = false;
    private List<InvoicePaperPanel.LineItem> currentInvoiceItems;
    private final JLabel lblVat = new JLabel("VAT (8%): 0 VND");
    private final JLabel lblSvc = new JLabel("Phí dịch vụ (15%): 0 VND");
    private final JLabel lblTongCong = new JLabel("Tổng cộng: 0 VND");

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

    private DefaultTableModel orderModel;
    private JTable orderTable;
    private final JLabel lblTongTien = new JLabel("0");
    private final JButton btnThanhToan = new JButton("Thanh toán");

    // Checkout UI
    private final InvoicePaperPanel invoicePanel = new InvoicePaperPanel();
    private final CheckoutSummaryPanel checkoutSummaryPanel = new CheckoutSummaryPanel();

    public OrderDialog(Window parent, int banId, String tenBan) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.banId = banId;
        this.tenBan = tenBan;
        this.userId = Session.getCurrentUser().getUserId();
        
        Integer servingUserId = chiTietBanController.getServingUserId(banId);

        if (servingUserId != null && servingUserId != userId) {

            JOptionPane.showMessageDialog(
                    this,
                    "Bàn này đang được nhân viên khác phục vụ."
            );

            dispose();
            return;
        }

        setTitle("Đặt món - " + tenBan);
        setSize(1200, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UITheme.BEIGE);
        setLayout(new BorderLayout(8, 8));

        orderModel = new DefaultTableModel(new Object[]{
                "CTID", "MONID", "Món", "Đơn giá", "SL", "Thành tiền", "+", "-", "Hủy"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7 || column == 8;
            }
        };
        orderTable = new JTable(orderModel);
        orderTable.setRowHeight(34);
        orderTable.setFont(orderTable.getFont().deriveFont(15f));
        orderTable.getTableHeader().setFont(orderTable.getTableHeader().getFont().deriveFont(Font.BOLD, 14f));
        orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setWidth(0, 0);   // CTID
        setWidth(1, 0);   // MONID

        setWidth(2, 140); // Món
        setWidth(3, 90);  // Đơn giá
        setWidth(4, 30);  // SL
        setWidth(5, 95); // Thành tiền

        setWidth(6, 45);  // +
        setWidth(7, 45);  // -
        setWidth(8, 70);  // Hủy
        // Hide CTID + MONID
        hideColumn(0);
        hideColumn(1);

        orderTable.getColumn("+")
                .setCellRenderer(
                    new TableButtonRenderer(UITheme.SUCCESS)
                );

        orderTable.getColumn("+")
                .setCellEditor(
                    new TableButtonEditor(
                        "+",
                        UITheme.SUCCESS,
                        this::onPlusRow
                    )
                );


        orderTable.getColumn("-")
                .setCellRenderer(
                    new TableButtonRenderer(UITheme.COFFEE)
                );

        orderTable.getColumn("-")
                .setCellEditor(
                    new TableButtonEditor(
                        "-",
                        UITheme.COFFEE,
                        this::onMinusRow
                    )
                );


        orderTable.getColumn("Hủy")
                .setCellRenderer(
                    new TableButtonRenderer(UITheme.DANGER)
                );

        orderTable.getColumn("Hủy")
                .setCellEditor(
                    new TableButtonEditor(
                        "Hủy",
                        UITheme.DANGER,
                        this::onHuyRow
                    )
                );
        //Resét độ rộngg 
        TableColumn plusCol = orderTable.getColumn("+");
        plusCol.setPreferredWidth(45);
        plusCol.setMinWidth(40);
        plusCol.setMaxWidth(50);

        TableColumn minusCol = orderTable.getColumn("-");
        minusCol.setPreferredWidth(45);
        minusCol.setMinWidth(40);
        minusCol.setMaxWidth(50);

        TableColumn huyCol = orderTable.getColumn("Hủy");
        huyCol.setPreferredWidth(60);
        huyCol.setMinWidth(55);
        huyCol.setMaxWidth(70);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftSide(), buildRightSide());
        split.setDividerLocation(650);
        add(split, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.setUI(new RoundedButtonUI());
        btnClose.setBackground(UITheme.LATTE);
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.BEIGE);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        loadLoaiButtons();
        refreshDishGrid();
        refreshOrder();
        ScrollUtils.apply(this);
        
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

        JLabel lblKeyword = new JLabel("Từ khóa (tên):");
        JButton btnSearch = new JButton("Tìm");

        btnSearch.setUI(new RoundedButtonUI());
        btnSearch.setBackground(UITheme.CARAMEL);
        btnSearch.setForeground(Color.WHITE);

        btnSearch.addActionListener(e -> refreshDishGrid());
        search.add(lblKeyword, BorderLayout.WEST);
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

        // ===== LEFT INFO =====
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(UITheme.BEIGE);

        JPanel rowTong = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowTong.setBackground(UITheme.BEIGE);
        rowTong.add(new JLabel("Tổng tiền hàng: "));
        rowTong.add(lblTongTien);
        rowTong.add(new JLabel(" VND"));

        JPanel rowVat = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowVat.setBackground(UITheme.BEIGE);
        rowVat.add(lblVat);

        JPanel rowSvc = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowSvc.setBackground(UITheme.BEIGE);
        rowSvc.add(lblSvc);

        JPanel rowTotal = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowTotal.setBackground(UITheme.BEIGE);

        lblTongCong.setFont(lblTongCong.getFont().deriveFont(Font.BOLD, 14f));

        rowTotal.add(lblTongCong);

        left.add(rowTong);
        left.add(Box.createVerticalStrut(4));
        left.add(rowVat);
        left.add(rowSvc);
        left.add(Box.createVerticalStrut(6));
        left.add(rowTotal);

        bottom.add(left, BorderLayout.WEST);

        // ===== BUTTON =====
        btnThanhToan.setUI(new RoundedButtonUI());
        btnThanhToan.setBackground(UITheme.CARAMEL);
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.addActionListener(e -> onThanhToan());
        btnThanhToan.setFocusPainted(false);
        btnThanhToan.setPreferredSize(new Dimension(150, 20));
        btnThanhToan.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnThanhToan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottom.add(btnThanhToan, BorderLayout.EAST);

        root.add(bottom, BorderLayout.SOUTH);

        // ===== UPDATE TOTALS =====
        orderModel.addTableModelListener(e -> {

            BigDecimal tong = BigDecimal.ZERO;

            for (int i = 0; i < orderModel.getRowCount(); i++) {

                String raw = String.valueOf(orderModel.getValueAt(i, 5));

                raw = raw.replace(".", "")
                        .replace(" VND", "")
                        .trim();

                try {
                    tong = tong.add(new BigDecimal(raw));
                } catch (Exception ignored) {
                }
            }

            BigDecimal vat = tong.multiply(BigDecimal.valueOf(0.08));
            BigDecimal svc = tong.multiply(BigDecimal.valueOf(0.15));
            BigDecimal total = tong.add(vat).add(svc);

            lblVat.setText("VAT (8%): " + MoneyUtils.formatVnd(vat) + " VND");
            lblSvc.setText("Phí dịch vụ (15%): " + MoneyUtils.formatVnd(svc) + " VND");
            lblTongCong.setText("Tổng cộng: " + MoneyUtils.formatVnd(total) + " VND");
        });

        return root;
    }

    private void loadLoaiButtons() {
        JPanel categories = findNamedPanel(leftCards, "categories");
        if (categories == null) {
            return;
        }
        categories.removeAll();

        JButton all = new JButton("Tất cả");
        all.setUI(new RoundedButtonUI());
        all.setBackground(UITheme.LATTE);
        all.setForeground(Color.WHITE);
        all.addActionListener(e -> {
            selectedLoaiId = null;
            refreshDishGrid();
        });
        categories.add(all);

        List<LoaiMonAn> list = loaiMonAnController.getAll();
        for (LoaiMonAn l : list) {
            JButton b = new JButton(l.getTenLoai());
            b.setUI(new RoundedButtonUI());
            b.setBackground(UITheme.CARAMEL);
            b.setForeground(Color.WHITE);
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

        RoundedPanel card = new RoundedPanel(18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(null);
        card.setBackground(isHet ? new Color(0xEEEEEE) : UITheme.SAND);
        card.setPreferredSize(new Dimension(170, 220));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel img = new JPanel(new BorderLayout());
        img.setPreferredSize(new Dimension(120, 120));
        img.setMaximumSize(new Dimension(120, 120));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);
        img.setOpaque(false);

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(120, 120));
        imgLabel.setMinimumSize(new Dimension(120, 120));
        imgLabel.setMaximumSize(new Dimension(120, 120));
        imgLabel.setOpaque(false);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        ImageIcon icon = tryLoadMonImage(m.getHinhAnh(), 120);
        if (icon != null) {
            imgLabel.setIcon(icon);
            imgLabel.setText("");
        } else if (m.getTenMon() != null && (m.getTenMon().trim().length() > 0)) {
            imgLabel.setText(m.getTenMon());
        }
        img.add(imgLabel, BorderLayout.CENTER);

        JLabel ten = new JLabel(String.valueOf(m.getTenMon()).toUpperCase());
        JLabel gia = new JLabel(MoneyUtils.formatVnd(m.getGia()) + " VND");

        ten.setAlignmentX(Component.CENTER_ALIGNMENT);
        gia.setAlignmentX(Component.CENTER_ALIGNMENT);
        ten.setBorder(BorderFactory.createEmptyBorder(6, 8, 0, 8));
        gia.setBorder(BorderFactory.createEmptyBorder(2, 8, 8, 8));
        ten.setHorizontalAlignment(SwingConstants.CENTER);
        gia.setHorizontalAlignment(SwingConstants.CENTER);

        if (isHet) {
            ten.setForeground(Color.GRAY);
            gia.setForeground(Color.GRAY);
            imgLabel.setForeground(Color.GRAY);
        }

        card.add(Box.createVerticalStrut(8));
        img.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                    card.setBackground(UITheme.SAND);
                }
            });
        } else {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        return card;
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

    private void configureOrderColumns() {
        orderTable.setRowHeight(34);
        orderTable.setFont(orderTable.getFont().deriveFont(15f));
        orderTable.getTableHeader().setFont(orderTable.getTableHeader().getFont().deriveFont(Font.BOLD, 14f));

        orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        setWidth(0,0);
        setWidth(1,0);

        setWidth(2,220); // Món
        setWidth(3,90);  // Đơn giá
        setWidth(4,40);  // SL
        setWidth(5,120); // Thành tiền
        setWidth(6,45);  // +
        setWidth(7,45);  // -
        setWidth(8,70);  // Hủy
    }

    private void setWidth(int viewColumnIndex, int width) {
        TableColumn col = orderTable.getColumnModel().getColumn(viewColumnIndex);
        col.setPreferredWidth(width);
        col.setMinWidth(width);
        col.setMaxWidth(width);
    }

    private ImageIcon tryLoadMonImageFallback(int monId, int size) {
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
                    "Hủy"
            });
        }

        lblTongTien.setText(MoneyUtils.formatVnd(tong));
        btnThanhToan.setEnabled(!items.isEmpty());

        checkoutSummaryPanel.setData(tenBan, items, monById);
        invoicePanel.setData(currentHoaDonId == null
                ? "CHO_THANH_TOAN"
                : String.valueOf(currentHoaDonId), 
                tenBan, 
                Session.getCurrentUser().getFullName(), 
                null, 
                toInvoiceItems(items, monById));
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
        int confirm = JOptionPane.showConfirmDialog(this, "Hủy món này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
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

        try {

            List<ChiTietBanDTO> items = chiTietBanController.getByBanAndUser(banId, userId);
            Map<Integer, MonAnWithPriceDTO> monById = new HashMap<>();
            for (MonAnWithPriceDTO m : monAnController.getAll()) {
                monById.put(m.getMonId(), m);
            }
            currentInvoiceItems = toInvoiceItems(items, monById);
            currentHoaDonId = hoaDonController.checkout(banId, userId);
            daThanhToan = true;
            invoicePanel.setData(
                    String.valueOf(currentHoaDonId),
                    tenBan,
                    Session.getCurrentUser().getFullName(),
                    LocalDateTime.now(),
                    currentInvoiceItems
            );

            try {
                banController.updateTrangThai(banId, "TRONG");
            } catch (Exception ignored) {
            }

            ((CardLayout) leftCards.getLayout())
                    .show(leftCards, "INVOICE");

            ((CardLayout) rightCards.getLayout())
                    .show(rightCards, "CHECKOUT");

            checkoutSummaryPanel.setOnHoanThanh(this::doHoanThanh);
            checkoutSummaryPanel.setOnExportPdf(this::onExportPdf);

            JOptionPane.showMessageDialog(
                    this,
                    "Da thanh toan ban "
                            + tenBan
                            + " (Hoa don "
                            + currentHoaDonId
                            + ")"
            );

        } catch (ServiceException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Loi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void doHoanThanh() {
        dispose();
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
        private Runnable onExportPdf;

        private final JTextArea summary = new JTextArea();
        private final JButton btnHoanThanh = new JButton("Hoàn thành");
        private final JButton btnExportPdf = new JButton("Xuất PDF");

        public CheckoutSummaryPanel() {
            setLayout(new BorderLayout(8, 8));
            setBackground(UITheme.BEIGE);
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            summary.setEditable(false);
            summary.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            summary.setBackground(Color.WHITE);
            JPanel centerWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));

            summary.setPreferredSize(new Dimension(320, 420));

            centerWrap.add(summary);

            add(centerWrap, BorderLayout.CENTER);

            btnHoanThanh.setUI(new RoundedButtonUI());
            btnHoanThanh.setBackground(UITheme.LATTE);
            btnHoanThanh.setForeground(Color.WHITE);

            btnExportPdf.setUI(new RoundedButtonUI());
            btnExportPdf.setBackground(UITheme.CARAMEL);
            btnExportPdf.setForeground(Color.WHITE);

            btnHoanThanh.addActionListener(e -> {
                if (onHoanThanh != null) {
                    onHoanThanh.run();
                }
            });

            btnExportPdf.addActionListener(e -> {
                if (onExportPdf != null) {
                    onExportPdf.run();
                }
            });
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            actions.setBackground(UITheme.BEIGE);

            actions.add(btnExportPdf);
            actions.add(btnHoanThanh);

            add(actions, BorderLayout.SOUTH);
        }

        public void setOnHoanThanh(Runnable r) {
            this.onHoanThanh = r;
        }

        public void setData(String tenBan, List<ChiTietBanDTO> items, Map<Integer, MonAnWithPriceDTO> monById) {
            BigDecimal tong = BigDecimal.ZERO;
            StringBuilder sb = new StringBuilder();
            sb.append("BÀN: ").append(tenBan).append("\n\n");
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
            sb.append("Phí dịch vụ 15%: ").append(MoneyUtils.formatVnd(svc)).append("\n");
            sb.append("Tổng cộng: ").append(MoneyUtils.formatVnd(total)).append("\n");
            summary.setText(sb.toString());
            summary.setCaretPosition(0);
        }

        public void setOnExportPdf(Runnable r) {
            this.onExportPdf = r;
        }
    }

    private void onExportPdf() {

            try {

                if (currentInvoiceItems == null
                        || currentInvoiceItems.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Khong co du lieu hoa don."
                    );

                    return;
                }

                // tạo folder nếu chưa có
                File folder = new File("assets/hoadon");

                if (!folder.exists()) {
                    folder.mkdirs();
                }

                String filePath =
                        "assets/hoadon/hoa_" + currentHoaDonId + "_"
                                + System.currentTimeMillis()
                                + ".pdf";

                PDFInvoiceUtil.exportInvoice(
                        filePath,
                        String.valueOf(currentHoaDonId),
                        tenBan,
                        Session.getCurrentUser().getFullName(),
                        LocalDateTime.now(),
                        currentInvoiceItems
                );

                hoaDonController.updateFilePdf(
                        currentHoaDonId,
                        filePath
                );

                JOptionPane.showMessageDialog(this,
                        "Da xuat PDF:\n" + filePath);

            } catch (Exception e) {

                e.printStackTrace();

                JOptionPane.showMessageDialog(this,
                        "Khong the xuat PDF.");
            }
        }
}
