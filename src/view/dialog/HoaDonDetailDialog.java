package view.dialog;

import controller.HoaDonController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HoaDonDetailDialog extends JDialog {
    private final HoaDonController controller = new HoaDonController();
    private final DefaultTableModel tableModel;

    public HoaDonDetailDialog(Window parent, int hoaDonId) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Chi tiết hóa đơn #" + hoaDonId);
        setSize(700, 420);
        setLocationRelativeTo(parent);

        tableModel = new DefaultTableModel(new Object[]{"Tên món", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        load(hoaDonId);
    }

    private void load(int hoaDonId) {
        List<Object[]> details = controller.getDetails(hoaDonId);
        tableModel.setRowCount(0);
        for (Object[] d : details) {
            tableModel.addRow(d);
        }
    }
}

