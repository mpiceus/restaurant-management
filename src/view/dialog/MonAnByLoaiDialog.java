package view.dialog;

import controller.MonAnController;
import model.MonAnWithPriceDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MonAnByLoaiDialog extends JDialog {
    private final MonAnController monAnController = new MonAnController();
    private final DefaultTableModel tableModel;

    public MonAnByLoaiDialog(Window parent, int loaiId, String tenLoai) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Món thuộc loại: " + tenLoai);
        setSize(800, 450);
        setLocationRelativeTo(parent);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Tên món", "Giá mới nhất", "Trạng thái"}, 0) {
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

        load(loaiId);
    }

    private void load(int loaiId) {
        List<MonAnWithPriceDTO> list = monAnController.search(null, loaiId, null);
        tableModel.setRowCount(0);
        for (MonAnWithPriceDTO m : list) {
            tableModel.addRow(new Object[]{m.getMonId(), m.getTenMon(), m.getGia(), m.getTrangThai()});
        }
    }
}

