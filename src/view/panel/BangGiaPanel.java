package view.panel;

import controller.BangGiaController;
import controller.MonAnController;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import model.BangGia;
import model.MonAnWithPriceDTO;
import service.ServiceException;
import util.RoundedButtonUI;
import util.ScrollUtils;
import util.UITheme;
import view.dialog.BangGiaFormDialog;

public class BangGiaPanel extends JPanel {
    private final BangGiaController controller = new BangGiaController();
    private final MonAnController monAnController = new MonAnController();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final Map<Integer, String> monNameById = new HashMap<>();

    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BangGiaPanel() {
        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Món ID", "Tên món", "Giá", "Ngày áp dụng"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(table.getFont().deriveFont(16f));
        table.setBackground(UITheme.SAND);
        table.setOpaque(true);
        table.setForeground(Color.DARK_GRAY);
        table.setSelectionBackground(UITheme.SAND.darker());
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setBackground(UITheme.COFFEE);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setForeground(Color.DARK_GRAY);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 15f));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(UITheme.SAND);
        scrollPane.setBackground(UITheme.SAND);

        add(scrollPane, BorderLayout.CENTER);

        configureColumns();

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Reset");
        JButton btnAdd = new JButton("Thêm");
        //JButton btnEdit = new JButton("Sửa");
        //JButton btnDelete = new JButton("Xóa");

        btnRefresh.setUI(new RoundedButtonUI());
        btnRefresh.setBackground(UITheme.LATTE);
        btnRefresh.setForeground(Color.WHITE);

        btnAdd.setUI(new RoundedButtonUI());
        btnAdd.setBackground(UITheme.CARAMEL);
        btnAdd.setForeground(Color.WHITE);

        /*btnEdit.setUI(new RoundedButtonUI());
        btnEdit.setBackground(UITheme.CARAMEL);
        btnEdit.setForeground(Color.WHITE);*/

       /*btnDelete.setUI(new RoundedButtonUI());
        btnDelete.setBackground(UITheme.CARAMEL);
        btnDelete.setForeground(Color.WHITE);*/

        btnRefresh.addActionListener(e -> loadData());
        btnAdd.addActionListener(e -> onAdd());
        //btnEdit.addActionListener(e -> onEdit());
        //btnDelete.addActionListener(e -> onDelete());

        bottom.add(btnRefresh);
        bottom.add(btnAdd);
        //bottom.add(btnEdit);
        //bottom.add(btnDelete);

        add(bottom, BorderLayout.SOUTH);

        ScrollUtils.apply(this);
        loadData();
    }

    private void configureColumns() {
        centerAndNarrow(table.getColumnModel().getColumn(0), 48);
        centerAndNarrow(table.getColumnModel().getColumn(1), 60);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
    }

    private void centerAndNarrow(TableColumn column, int width) {
        column.setPreferredWidth(width);
        column.setMinWidth(Math.max(36, width - 8));
        column.setMaxWidth(width + 8);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        column.setCellRenderer(renderer);
    }

    private void loadMonMap() {
        monNameById.clear();
        List<MonAnWithPriceDTO> monList = monAnController.getAll();
        for (MonAnWithPriceDTO m : monList) {
            monNameById.put(m.getMonId(), m.getTenMon());
        }
    }

    private void loadData() {
        loadMonMap();
        List<BangGia> list = controller.getAll();
        tableModel.setRowCount(0);
        for (BangGia bg : list) {
            tableModel.addRow(new Object[]{
                    bg.getBangGiaId(),
                    bg.getMonId(),
                    monNameById.getOrDefault(bg.getMonId(), ""),
                    bg.getGia(),
                    bg.getNgayApDung() == null ? "" : bg.getNgayApDung().format(dateFmt)
            });
        }
    }

    private Integer getSelectedBangGiaId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return (Integer) tableModel.getValueAt(row, 0);
    }

    private void onAdd() {
        BangGiaFormDialog dialog = new BangGiaFormDialog(SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        try {
            controller.create(dialog.getMonId(), dialog.getGia(), dialog.getNgayApDung());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*private void onEdit() {
        Integer bangGiaId = getSelectedBangGiaId();
        if (bangGiaId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng bảng giá để sửa.");
            return;
        }

        int row = table.getSelectedRow();
        int monId = (Integer) tableModel.getValueAt(row, 1);
        String giaStr = String.valueOf(tableModel.getValueAt(row, 3));
        String ngayStr = String.valueOf(tableModel.getValueAt(row, 4));

        BangGiaFormDialog.InitialData init = new BangGiaFormDialog.InitialData(bangGiaId, monId, giaStr, ngayStr);
        BangGiaFormDialog dialog = new BangGiaFormDialog(SwingUtilities.getWindowAncestor(this), init);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }

        try {
            controller.update(bangGiaId, dialog.getMonId(), dialog.getGia(), dialog.getNgayApDung());
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }*/

    /*private void onDelete() {
        Integer bangGiaId = getSelectedBangGiaId();
        if (bangGiaId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng bảng giá để xóa.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa bảng giá id=" + bangGiaId + " ?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            controller.delete(bangGiaId);
            loadData();
        } catch (ServiceException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }*/
}
