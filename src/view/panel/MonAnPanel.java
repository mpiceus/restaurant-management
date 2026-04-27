package view.panel;

import controller.LoaiMonAnController;
import controller.MonAnController;
import model.LoaiMonAn;
import model.MonAnWithPriceDTO;
import service.ServiceException;
import view.dialog.MonAnFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonAnPanel extends JPanel {
    private final boolean editable; // ADMIN: true, STAFF: false

    private final MonAnController monAnController = new MonAnController();
    private final LoaiMonAnController loaiController = new LoaiMonAnController();

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtKeyword = new JTextField();
    private final JTextField txtMonId = new JTextField();
    private final JComboBox<LoaiMonAn> cbLoai = new JComboBox<>();

    private final Map<Integer, LoaiMonAn> loaiById = new HashMap<>();

    public MonAnPanel(boolean editable) {
        this.editable = editable;
        setLayout(new BorderLayout(8, 8));

        JPanel top = buildTop();
        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tên món", "Loại", "Giá mới nhất", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = buildBottom();
        add(bottom, BorderLayout.SOUTH);

        loadLoai();
        loadData(null, null, null);
    }

    private JPanel buildTop() {
        JPanel p = new JPanel(new GridLayout(2, 4, 8, 8));
        p.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        p.add(new JLabel("Từ khóa (tên):"));
        p.add(txtKeyword);

        p.add(new JLabel("Loại:"));
        p.add(cbLoai);

        p.add(new JLabel("Mon ID:"));
        p.add(txtMonId);

        JButton btnSearch = new JButton("Tìm");
        JButton btnReset = new JButton("Reset");

        btnSearch.addActionListener(e -> onSearch());
        btnReset.addActionListener(e -> {
            txtKeyword.setText("");
            txtMonId.setText("");
            cbLoai.setSelectedIndex(0);
            loadData(null, null, null);
        });

        p.add(btnReset);
        p.add(btnSearch);

        return p;
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData(null, null, null));
        p.add(btnRefresh);

        if (editable) {
            JButton btnAdd = new JButton("Thêm");
            JButton btnEdit = new JButton("Sửa");
            JButton btnDelete = new JButton("Xóa");

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
        loaiById.clear();

        List<LoaiMonAn> loaiList = loaiController.getAll();
        for (LoaiMonAn l : loaiList) {
            loaiById.put(l.getLoaiId(), l);
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
        tableModel.setRowCount(0);
        for (MonAnWithPriceDTO m : list) {
            tableModel.addRow(new Object[]{
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

        loadData(keyword, loaiId, monId);
    }

    private Integer getSelectedMonId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) tableModel.getValueAt(row, 0);
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
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        Integer monId = getSelectedMonId();
        if (monId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 món để sửa.");
            return;
        }

        // Lấy dữ liệu từ bảng (đơn giản)
        int row = table.getSelectedRow();
        String tenMon = String.valueOf(tableModel.getValueAt(row, 1));
        String tenLoai = String.valueOf(tableModel.getValueAt(row, 2));
        String trangThai = String.valueOf(tableModel.getValueAt(row, 4));

        LoaiMonAn loai = null;
        for (LoaiMonAn l : loaiById.values()) {
            if (l.getTenLoai().equals(tenLoai)) {
                loai = l;
                break;
            }
        }

        MonAnFormDialog dialog = new MonAnFormDialog(
                SwingUtilities.getWindowAncestor(this),
                loaiController,
                new MonAnFormDialog.InitialData(monId, tenMon, loai == null ? 0 : loai.getLoaiId(), trangThai)
        );
        dialog.setVisible(true);

        if (!dialog.isSaved()) {
            return;
        }

        try {
            BigDecimal giaMoi = dialog.getGiaOptional();
            monAnController.update(monId, dialog.getTenMon(), dialog.getLoaiId(), dialog.getTrangThai(), giaMoi);
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Integer monId = getSelectedMonId();
        if (monId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 món để xóa.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa món id=" + monId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            monAnController.delete(monId);
            loadData(null, null, null);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

