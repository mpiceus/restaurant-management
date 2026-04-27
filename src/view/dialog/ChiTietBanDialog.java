package view.dialog;

import controller.ChiTietBanController;
import controller.LoaiMonAnController;
import controller.MonAnController;
import model.ChiTietBanDTO;
import model.LoaiMonAn;
import model.MonAnWithPriceDTO;
import service.ServiceException;
import util.Session;
import view.common.TableButtonEditor;
import view.common.TableButtonRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Order tạm (ChiTietBan) cho STAFF.
 * <p>
 * Chia 2 cột:
 * - Left: danh sách món đã gọi (của nhân viên hiện tại)
 * - Right: search + chọn món từ danh sách để thêm
 */
public class ChiTietBanDialog extends JDialog {
    private final int banId;
    private final int userId;

    private final ChiTietBanController chiTietBanController = new ChiTietBanController();
    private final MonAnController monAnController = new MonAnController();
    private final LoaiMonAnController loaiController = new LoaiMonAnController();

    private final DefaultTableModel orderModel;
    private final JTable orderTable;

    private final DefaultTableModel monModel;
    private final JTable monTable;

    private final JTextField txtKeyword = new JTextField();
    private final JTextField txtMonId = new JTextField();
    private final JComboBox<LoaiMonAn> cbLoai = new JComboBox<>();
    private final JSpinner spSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ChiTietBanDialog(Window parent, int banId, String tenBan) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.banId = banId;
        this.userId = Session.getCurrentUser().getUserId();

        setTitle("Gọi món - " + tenBan + " (Bàn " + banId + ")");
        setSize(1100, 600);
        setLocationRelativeTo(parent);

        orderModel = new DefaultTableModel(new Object[]{"ID", "Món", "Số lượng", "Thời gian", "Sửa", "Xóa"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };
        orderTable = new JTable(orderModel);

        // Button columns
        orderTable.getColumn("Sửa").setCellRenderer(new TableButtonRenderer());
        orderTable.getColumn("Sửa").setCellEditor(new TableButtonEditor("Sửa", this::onEditOrderRow));
        orderTable.getColumn("Xóa").setCellRenderer(new TableButtonRenderer());
        orderTable.getColumn("Xóa").setCellEditor(new TableButtonEditor("Xóa", this::onDeleteOrderRow));

        monModel = new DefaultTableModel(new Object[]{"ID", "Tên món", "Loại", "Giá", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        monTable = new JTable(monModel);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeft(), buildRight());
        split.setDividerLocation(520);
        add(split, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        loadLoai();
        loadOrders();
        loadMonList(null, null, null);
    }

    private JPanel buildLeft() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createTitledBorder("Món đã gọi (của bạn)"));
        p.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRight() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createTitledBorder("Thêm món"));

        JPanel search = new JPanel(new GridLayout(3, 4, 8, 8));
        search.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        search.add(new JLabel("Từ khóa (tên):"));
        search.add(txtKeyword);
        search.add(new JLabel("Loại:"));
        search.add(cbLoai);

        search.add(new JLabel("Mon ID:"));
        search.add(txtMonId);
        JButton btnSearch = new JButton("Tìm");
        JButton btnReset = new JButton("Reset");
        btnSearch.addActionListener(e -> onSearch());
        btnReset.addActionListener(e -> {
            txtKeyword.setText("");
            txtMonId.setText("");
            cbLoai.setSelectedIndex(0);
            loadMonList(null, null, null);
        });
        search.add(btnReset);
        search.add(btnSearch);

        search.add(new JLabel("Số lượng:"));
        search.add(spSoLuong);
        JButton btnAdd = new JButton("Thêm vào bàn");
        btnAdd.addActionListener(e -> onAddMon());
        search.add(new JLabel());
        search.add(btnAdd);

        p.add(search, BorderLayout.NORTH);
        p.add(new JScrollPane(monTable), BorderLayout.CENTER);
        return p;
    }

    private void loadLoai() {
        cbLoai.removeAllItems();
        cbLoai.addItem(new LoaiMonAn(0, "Tất cả"));
        List<LoaiMonAn> list = loaiController.getAll();
        for (LoaiMonAn l : list) {
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

    private void loadOrders() {
        List<ChiTietBanDTO> list = chiTietBanController.getByBanAndUser(banId, userId);
        orderModel.setRowCount(0);
        for (ChiTietBanDTO c : list) {
            orderModel.addRow(new Object[]{
                    c.getChiTietBanId(),
                    c.getTenMon(),
                    c.getSoLuong(),
                    c.getThoiGian() == null ? "" : c.getThoiGian().format(timeFmt),
                    "Sửa",
                    "Xóa"
            });
        }
    }

    private void loadMonList(String keyword, Integer loaiId, Integer monId) {
        List<MonAnWithPriceDTO> list = monAnController.search(keyword, loaiId, monId);
        monModel.setRowCount(0);
        for (MonAnWithPriceDTO m : list) {
            monModel.addRow(new Object[]{
                    m.getMonId(),
                    m.getTenMon(),
                    m.getTenLoai(),
                    m.getGia(),
                    m.getTrangThai()
            });
        }
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
            JOptionPane.showMessageDialog(this, "Mon ID không hợp lệ.");
            return;
        }

        LoaiMonAn selected = (LoaiMonAn) cbLoai.getSelectedItem();
        Integer loaiId = (selected != null && selected.getLoaiId() != 0) ? selected.getLoaiId() : null;

        loadMonList(keyword, loaiId, monId);
    }

    private Integer getSelectedMonId() {
        int row = monTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) monModel.getValueAt(row, 0);
    }

    private void onAddMon() {
        Integer monId = getSelectedMonId();
        if (monId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 món từ danh sách bên phải.");
            return;
        }
        int soLuong = (Integer) spSoLuong.getValue();

        try {
            chiTietBanController.addMon(banId, monId, soLuong, userId);
            loadOrders();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEditOrderRow(int row) {
        int chiTietBanId = (Integer) orderModel.getValueAt(row, 0);
        int currentQty = Integer.parseInt(String.valueOf(orderModel.getValueAt(row, 2)));

        String s = JOptionPane.showInputDialog(this, "Nhập số lượng mới:", currentQty);
        if (s == null) {
            return;
        }
        int newQty;
        try {
            newQty = Integer.parseInt(s.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.");
            return;
        }

        try {
            chiTietBanController.updateSoLuong(chiTietBanId, newQty);
            loadOrders();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteOrderRow(int row) {
        int chiTietBanId = (Integer) orderModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa dòng order id=" + chiTietBanId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            chiTietBanController.delete(chiTietBanId);
            loadOrders();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

