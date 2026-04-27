package view.panel;

import controller.LoaiMonAnController;
import model.LoaiMonAn;
import service.ServiceException;
import view.dialog.LoaiMonAnFormDialog;
import view.dialog.MonAnByLoaiDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoaiMonAnPanel extends JPanel {
    private final boolean editable; // ADMIN: true, STAFF: false

    private final LoaiMonAnController controller = new LoaiMonAnController();

    private final DefaultTableModel tableModel;
    private final JTable table;

    public LoaiMonAnPanel(boolean editable) {
        this.editable = editable;
        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tên loại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = buildBottom();
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        p.add(btnRefresh);

        JButton btnView = new JButton("Xem món theo loại");
        btnView.addActionListener(e -> onViewMon());
        p.add(btnView);

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

    private void loadData() {
        List<LoaiMonAn> list = controller.getAll();
        tableModel.setRowCount(0);
        for (LoaiMonAn l : list) {
            tableModel.addRow(new Object[]{l.getLoaiId(), l.getTenLoai()});
        }
    }

    private Integer getSelectedLoaiId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) tableModel.getValueAt(row, 0);
    }

    private String getSelectedLoaiName() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return String.valueOf(tableModel.getValueAt(row, 1));
    }

    private void onViewMon() {
        Integer loaiId = getSelectedLoaiId();
        if (loaiId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 loại.");
            return;
        }
        String tenLoai = getSelectedLoaiName();
        MonAnByLoaiDialog dialog = new MonAnByLoaiDialog(SwingUtilities.getWindowAncestor(this), loaiId, tenLoai);
        dialog.setVisible(true);
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
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        Integer loaiId = getSelectedLoaiId();
        if (loaiId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 loại để sửa.");
            return;
        }
        String tenLoai = getSelectedLoaiName();

        LoaiMonAnFormDialog dialog = new LoaiMonAnFormDialog(SwingUtilities.getWindowAncestor(this), tenLoai);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            controller.update(loaiId, dialog.getTenLoai());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Integer loaiId = getSelectedLoaiId();
        if (loaiId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 loại để xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa loại id=" + loaiId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.delete(loaiId);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

