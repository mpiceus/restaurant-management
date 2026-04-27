package view.panel;

import controller.BanController;
import controller.HoaDonController;
import model.Ban;
import service.ServiceException;
import util.Session;
import view.common.TableButtonEditor;
import view.common.TableButtonRenderer;
import view.dialog.BanFormDialog;
import view.dialog.ChiTietBanDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BanPanel extends JPanel {
    private final boolean adminMode;
    private final BanController banController = new BanController();
    private final HoaDonController hoaDonController = new HoaDonController();

    private final DefaultTableModel tableModel;
    private final JTable table;

    public BanPanel(boolean adminMode) {
        this.adminMode = adminMode;
        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tên bàn", "Trạng thái", "Gọi món", "Thanh toán"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };
        table = new JTable(tableModel);

        // Ẩn ID (vẫn dùng để thao tác)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumn("Gọi món").setCellRenderer(new TableButtonRenderer());
        table.getColumn("Gọi món").setCellEditor(new TableButtonEditor("Gọi món", this::onGoiMonRow));

        table.getColumn("Thanh toán").setCellRenderer(new TableButtonRenderer());
        table.getColumn("Thanh toán").setCellEditor(new TableButtonEditor("Thanh toán", this::onThanhToanRow));

        add(new JScrollPane(table), BorderLayout.CENTER);

        add(buildBottom(), BorderLayout.SOUTH);

        loadData();
    }

    private JPanel buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        p.add(btnRefresh);

        if (adminMode) {
            JButton btnAdd = new JButton("Thêm bàn");
            JButton btnEdit = new JButton("Sửa bàn");
            JButton btnDelete = new JButton("Xóa bàn");

            btnAdd.addActionListener(e -> onAdd());
            btnEdit.addActionListener(e -> onEdit());
            btnDelete.addActionListener(e -> onDelete());

            p.add(btnAdd);
            p.add(btnEdit);
            p.add(btnDelete);
        } else {
            JButton btnUpdateStatus = new JButton("Cập nhật trạng thái");
            btnUpdateStatus.addActionListener(e -> onUpdateStatus());
            p.add(btnUpdateStatus);
        }

        return p;
    }

    private void loadData() {
        List<Ban> list = banController.getAll();
        tableModel.setRowCount(0);
        for (Ban b : list) {
            tableModel.addRow(new Object[]{
                    b.getBanId(),
                    b.getTenBan(),
                    b.getTrangThai(),
                    "Gọi món",
                    "Thanh toán"
            });
        }
    }

    private Integer getSelectedBanId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) tableModel.getValueAt(row, 0);
    }

    private String getSelectedTenBan() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return String.valueOf(tableModel.getValueAt(row, 1));
    }

    private String getSelectedTrangThai() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return String.valueOf(tableModel.getValueAt(row, 2));
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
        Integer banId = getSelectedBanId();
        if (banId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bàn để sửa.");
            return;
        }
        String tenBan = getSelectedTenBan();
        String trangThai = getSelectedTrangThai();

        BanFormDialog dialog = new BanFormDialog(SwingUtilities.getWindowAncestor(this),
                new BanFormDialog.InitialData(banId, tenBan, trangThai));
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            banController.update(banId, dialog.getTenBan(), dialog.getTrangThai());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Integer banId = getSelectedBanId();
        if (banId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bàn để xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa bàn id=" + banId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            banController.delete(banId);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateStatus() {
        Integer banId = getSelectedBanId();
        if (banId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 bàn.");
            return;
        }
        String current = getSelectedTrangThai();

        String[] options = new String[]{"TRONG", "DANG_PHUC_VU"};
        String newStatus = (String) JOptionPane.showInputDialog(this, "Chọn trạng thái:", "Cập nhật trạng thái",
                JOptionPane.PLAIN_MESSAGE, null, options, current);
        if (newStatus == null) {
            return;
        }

        try {
            banController.updateTrangThai(banId, newStatus);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onGoiMonRow(int row) {
        int banId = (Integer) tableModel.getValueAt(row, 0);
        String tenBan = String.valueOf(tableModel.getValueAt(row, 1));

        // STAFF mới được tạo order tạm
        if (!adminMode && Session.getCurrentUser() != null) {
            ChiTietBanDialog dialog = new ChiTietBanDialog(SwingUtilities.getWindowAncestor(this), banId, tenBan);
            dialog.setVisible(true);

            // sau khi gọi món, set trạng thái bàn (dễ hiểu)
            try {
                banController.updateTrangThai(banId, "DANG_PHUC_VU");
                loadData();
            } catch (Exception ignored) {
            }
        } else if (adminMode) {
            JOptionPane.showMessageDialog(this, "Chức năng gọi món ưu tiên cho STAFF theo yêu cầu.");
        }
    }

    private void onThanhToanRow(int row) {
        int banId = (Integer) tableModel.getValueAt(row, 0);

        if (adminMode) {
            JOptionPane.showMessageDialog(this, "Chức năng thanh toán ưu tiên cho STAFF theo yêu cầu.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Thanh toán bàn id=" + banId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int userId = Session.getCurrentUser().getUserId();
            int hoaDonId = hoaDonController.checkout(banId, userId);

            // set bàn về TRONG
            try {
                banController.updateTrangThai(banId, "TRONG");
            } catch (Exception ignored) {
            }

            loadData();
            JOptionPane.showMessageDialog(this, "Thanh toán thành công. Hóa đơn id=" + hoaDonId);
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

