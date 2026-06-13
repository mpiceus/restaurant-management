package view.panel;

import controller.HoaDonController;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import model.HoaDonDTO;
import util.RoundedButtonUI;
import util.ScrollUtils;
import util.Session;
import util.UITheme;
import view.common.TableButtonEditor;
import view.common.TableButtonRenderer;
import view.dialog.HoaDonDetailDialog;

public class HoaDonPanel extends JPanel {
    private final boolean adminMode;
    private final HoaDonController controller = new HoaDonController();

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HoaDonPanel(boolean adminMode) {
        this.adminMode = adminMode;
        setLayout(new BorderLayout(8, 8));
        setBackground(util.UITheme.BEIGE);

        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Bàn", "Nhân viên", "Tổng tiền", "Ngày tạo", "Chi tiết", "PDF"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6;
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
        table.getTableHeader().setReorderingAllowed(false);

       table.getColumn("Chi tiết")
        .setCellRenderer(
                new TableButtonRenderer(UITheme.CARAMEL)
        );

        table.getColumn("Chi tiết")
                .setCellEditor(
                        new TableButtonEditor(
                                "Xem",
                                UITheme.CARAMEL,
                                this::onViewDetailRow
                        )
                );


        table.getColumn("PDF")
                .setCellRenderer(
                        new TableButtonRenderer(UITheme.COFFEE)
                );

        table.getColumn("PDF")
                .setCellEditor(
                        new TableButtonEditor(
                                "Mở",
                                UITheme.COFFEE,
                                this::onOpenPdfRow
                        )
                );

        add(new JScrollPane(table), BorderLayout.CENTER);
        configureColumns();

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(util.UITheme.BEIGE);
        JButton btnRefresh = new JButton("Reset");
        btnRefresh.setUI(new RoundedButtonUI());
        btnRefresh.setBackground(UITheme.LATTE);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.addActionListener(e -> loadData());
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        ScrollUtils.apply(this);
        loadData();
    }

    private void configureColumns() {
        centerAndNarrow(table.getColumnModel().getColumn(0), 48);
        centerAndNarrow(table.getColumnModel().getColumn(1), 72);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);
    }

    private void centerAndNarrow(TableColumn column, int width) {
        column.setPreferredWidth(width);
        column.setMinWidth(Math.max(36, width - 8));
        column.setMaxWidth(width + 8);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        column.setCellRenderer(renderer);
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
                    "Xem",
                    "Mở"
            });
        }
    }

    private void onViewDetailRow(int row) {
        int hoaDonId = (Integer) tableModel.getValueAt(row, 0);
        HoaDonDetailDialog dialog = new HoaDonDetailDialog(SwingUtilities.getWindowAncestor(this), hoaDonId);
        dialog.setVisible(true);
    }

    private void onOpenPdfRow(int row) {
        int hoaDonId = (Integer) tableModel.getValueAt(row, 0);
        try {
            HoaDonDTO hd = controller.getById(hoaDonId);
            if (hd.getFilePdf() == null || hd.getFilePdf().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hóa đơn này chưa có file PDF.");
                return;
            }
            File file = new File(hd.getFilePdf());
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy file PDF.");
                return;
            }
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể mở file PDF.");
        }
    }
}
