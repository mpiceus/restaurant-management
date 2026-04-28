package view.dialog;

import controller.HoaDonController;
import model.HoaDonDTO;
import view.common.InvoicePaperPanel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDetailDialog extends JDialog {
    private final HoaDonController controller = new HoaDonController();
    private final InvoicePaperPanel invoice = new InvoicePaperPanel();

    public HoaDonDetailDialog(Window parent, int hoaDonId) {
        super(parent, ModalityType.APPLICATION_MODAL);
        setTitle("Chi tiet hoa don #" + hoaDonId);
        setSize(720, 620);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        add(invoice, BorderLayout.CENTER);

        JButton btnClose = new JButton("Dong");
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        load(hoaDonId);
    }

    private void load(int hoaDonId) {
        HoaDonDTO h = controller.getById(hoaDonId);
        List<Object[]> details = controller.getDetails(hoaDonId);

        List<InvoicePaperPanel.LineItem> items = new ArrayList<>();
        for (Object[] d : details) {
            String tenMon = String.valueOf(d[0]);
            int soLuong = ((Number) d[1]).intValue();
            BigDecimal donGia = (BigDecimal) d[2];
            items.add(new InvoicePaperPanel.LineItem(tenMon, soLuong, donGia));
        }

        String tenBan = h == null ? "" : h.getTenBan();
        String nv = h == null ? "" : h.getTenNhanVien();
        invoice.setData(String.valueOf(hoaDonId), tenBan, nv, h == null ? null : h.getNgayTao(), items);
    }
}

