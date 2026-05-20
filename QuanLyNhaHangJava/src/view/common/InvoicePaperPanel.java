package view.common;

import util.MoneyUtils;
import util.UITheme;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoicePaperPanel extends JPanel {
    public static class LineItem {
        public final String tenMon;
        public final int soLuong;
        public final BigDecimal donGia;

        public LineItem(String tenMon, int soLuong, BigDecimal donGia) {
            this.tenMon = tenMon;
            this.soLuong = soLuong;
            this.donGia = donGia == null ? BigDecimal.ZERO : donGia;
        }
    }

    private final JTextPane pane = new JTextPane();
    private final StyledDocument doc = pane.getStyledDocument();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Style normal;
    private final Style bold;

    public InvoicePaperPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BEIGE);

        pane.setEditable(false);
        pane.setBackground(Color.WHITE);
        pane.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        normal = pane.addStyle("normal", null);
        StyleConstants.setFontFamily(normal, Font.MONOSPACED);
        StyleConstants.setFontSize(normal, 12);

        bold = pane.addStyle("bold", normal);
        StyleConstants.setBold(bold, true);

        JPanel paper = new JPanel(new BorderLayout());
        paper.setBackground(Color.WHITE);
        paper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCCCCCC)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        paper.setPreferredSize(new Dimension(380, 560));
        paper.add(pane, BorderLayout.CENTER);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setBackground(UITheme.BEIGE);
        center.add(paper);

        JScrollPane scroll = new JScrollPane(center);
        scroll.getViewport().setBackground(UITheme.BEIGE);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void setData(String maHoaDonOrNull,
                        String tenBan,
                        String nhanVien,
                        LocalDateTime gioThanhToanOrNow,
                        List<LineItem> items) {
        clear();

        LocalDateTime t = gioThanhToanOrNow == null ? LocalDateTime.now() : gioThanhToanOrNow;
        String ma = maHoaDonOrNull == null ? "" : maHoaDonOrNull;

        append("Umami Bam\n", bold);
        append("28a Xom Ha Hoi, Tran Hung Dao\n\n", normal);
        append(center("HOA DON", 29) + "\n\n", bold);

        append("Ma hoa don: " + ma + "\n", normal);
        append("Gio: " + t.format(fmt) + "\n", normal);
        append("Ban: " + (tenBan == null ? "" : tenBan) + "\n", normal);
        append("Nhan vien: " + (nhanVien == null ? "" : nhanVien) + "\n", normal);

        String sep = "-----------------------------\n";
        append(sep, normal);

        append(String.format("%-16s %5s %10s\n", "Ten mon", "SL", "Thanh tien"), bold);
        append(sep, normal);

        BigDecimal tong = BigDecimal.ZERO;
        for (LineItem it : safe(items)) {
            BigDecimal thanhTien = it.donGia.multiply(BigDecimal.valueOf(it.soLuong));
            tong = tong.add(thanhTien);
            append(String.format("%-16s %5d %10s\n",
                    safeCut(it.tenMon, 16),
                    it.soLuong,
                    MoneyUtils.formatVnd(thanhTien)), normal);
        }

        append(sep, normal);

        BigDecimal vat = tong.multiply(BigDecimal.valueOf(0.08));
        BigDecimal svc = tong.multiply(BigDecimal.valueOf(0.15));
        BigDecimal total = tong.add(vat).add(svc);

        append(String.format("%-22s %10s\n", "Tong thanh tien", MoneyUtils.formatVnd(tong)), normal);
        append(String.format("%-22s %10s\n", "VAT (8%)", MoneyUtils.formatVnd(vat)), normal);
        append(String.format("%-22s %10s\n", "Phi dich vu (15%)", MoneyUtils.formatVnd(svc)), normal);
        append(sep, normal);
        append(String.format("%-22s %10s\n", "Tong cong", MoneyUtils.formatVnd(total)), bold);

        pane.setCaretPosition(0);
    }

    private void clear() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ignored) {
        }
    }

    private void append(String s, Style style) {
        try {
            doc.insertString(doc.getLength(), s, style);
        } catch (BadLocationException ignored) {
        }
    }

    private static String safeCut(String s, int max) {
        if (s == null) {
            return "";
        }
        String x = s.trim();
        if (x.length() <= max) {
            return x;
        }
        return x.substring(0, max - 1) + ".";
    }

    private static String center(String s, int width) {
        if (s == null) {
            return "";
        }
        if (s.length() >= width) {
            return s;
        }
        int left = (width - s.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) {
            sb.append(' ');
        }
        sb.append(s);
        return sb.toString();
    }

    private static List<LineItem> safe(List<LineItem> items) {
        return items == null ? new ArrayList<>() : items;
    }
}

