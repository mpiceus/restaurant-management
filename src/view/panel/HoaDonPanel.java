package view.panel;

import controller.HoaDonController;
import model.HoaDonDTO;
import util.Session;
import view.common.TableButtonEditor;
import view.common.TableButtonRenderer;
import view.dialog.HoaDonDetailDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Hóa đơn:
 * - Không cho sửa / xóa
 * - ADMIN: xem tất cả
 * - STAFF: chỉ xem hóa đơn của mình
 */
public class HoaDonPanel extends JPanel {
    private final boolean adminMode;
    private final HoaDonController controller = new HoaDonController();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HoaDonPanel(boolean adminMode) {
        this.adminMode = adminMode;
        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Bàn", "Nhân viên", "Tổng tiền", "Ngày tạo", "Chi tiết"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        table = new JTable(tableModel);

        table.getColumn("Chi tiết").setCellRenderer(new TableButtonRenderer());
        table.getColumn("Chi tiết").setCellEditor(new TableButtonEditor("Xem", this::onViewDetailRow));

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        List<HoaDonDTO> list;
        if (adminMode) {
            list = controller.getAll();
        } else {
            list = controller.getByUser(Session.getCurrentUser().getUserId());
        }

        tableModel.setRowCount(0);
        for (HoaDonDTO h : list) {
            tableModel.addRow(new Object[]{
                    h.getHoaDonId(),
                    h.getTenBan(),
                    h.getTenNhanVien(),
                    h.getTongTien(),
                    h.getNgayTao() == null ? "" : h.getNgayTao().format(timeFmt),
                    "Xem"
            });
        }
    }

    private void onViewDetailRow(int row) {
        int hoaDonId = (Integer) tableModel.getValueAt(row, 0);
        HoaDonDetailDialog dialog = new HoaDonDetailDialog(SwingUtilities.getWindowAncestor(this), hoaDonId);
        dialog.setVisible(true);
    }
}

