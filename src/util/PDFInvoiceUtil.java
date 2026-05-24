package util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import view.common.InvoicePaperPanel;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFInvoiceUtil {

    public static void exportInvoice(
            String filePath,
            String maHoaDon,
            String tenBan,
            String nhanVien,
            LocalDateTime gio,
            List<InvoicePaperPanel.LineItem> items
    ) throws Exception {

        // Kho giấy nhỏ dài như bill thật
        Rectangle pageSize = new Rectangle(226, 800);

        Document document = new Document(
                pageSize,
                12,
                12,
                12,
                12
        );

        PdfWriter.getInstance(
                document,
                new FileOutputStream(filePath)
        );

        document.open();

        // FONT
        Font titleFont = FontFactory.getFont(
                FontFactory.COURIER_BOLD,
                15
        );

        Font normalFont = FontFactory.getFont(
                FontFactory.COURIER,
                9
        );

        Font boldFont = FontFactory.getFont(
                FontFactory.COURIER_BOLD,
                9
        );

        Font totalFont = FontFactory.getFont(
                FontFactory.COURIER_BOLD,
                10
        );

        // FORMAT TIME
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String time = gio.format(fmt);

        // TÍNH TIỀN
        BigDecimal tong = BigDecimal.ZERO;

        for (InvoicePaperPanel.LineItem item : items) {

            BigDecimal thanhTien =
                    item.donGia.multiply(
                            BigDecimal.valueOf(item.soLuong)
                    );

            tong = tong.add(thanhTien);
        }

        BigDecimal vat =
                tong.multiply(BigDecimal.valueOf(0.08));

        BigDecimal svc =
                tong.multiply(BigDecimal.valueOf(0.15));

        BigDecimal total =
                tong.add(vat).add(svc);

        // HEADER
        Paragraph shopTitle =
                new Paragraph("UMAMI BAM", titleFont);

        shopTitle.setAlignment(Element.ALIGN_CENTER);

        document.add(shopTitle);

        Paragraph address =
                new Paragraph(
                        "28a Xom Ha Hoi, Tran Hung Dao",
                        normalFont
                );

        address.setAlignment(Element.ALIGN_CENTER);

        document.add(address);

        document.add(new Paragraph(" "));

        // INFO
        document.add(new Paragraph(
                "Ma hoa don: " + maHoaDon,
                normalFont
        ));

        document.add(new Paragraph(
                "Gio: " + time,
                normalFont
        ));

        document.add(new Paragraph(
                "Ban: " + tenBan,
                normalFont
        ));

        document.add(new Paragraph(
                "Nhan vien: " + nhanVien,
                normalFont
        ));

        document.add(new Paragraph(
                "----------------------------------",
                normalFont
        ));

        // TABLE HEADER
        document.add(new Paragraph(
                String.format(
                        "%-16s %4s %10s",
                        "Ten mon",
                        "SL",
                        "Thanh tien"
                ),
                boldFont
        ));

        document.add(new Paragraph(
                "----------------------------------",
                normalFont
        ));

        // ITEMS
        for (InvoicePaperPanel.LineItem item : items) {

            BigDecimal thanhTien =
                    item.donGia.multiply(
                            BigDecimal.valueOf(item.soLuong)
                    );

            String line = String.format(
                    "%-16s %4d %10s",
                    safe(item.tenMon, 16),
                    item.soLuong,
                    MoneyUtils.formatVnd(thanhTien)
            );

            document.add(new Paragraph(
                    line,
                    normalFont
            ));
        }

        document.add(new Paragraph(
                "----------------------------------",
                normalFont
        ));

        // TOTALS
        document.add(new Paragraph(
                String.format(
                        "%-22s %10s",
                        "Tong thanh tien",
                        MoneyUtils.formatVnd(tong)
                ),
                normalFont
        ));

        document.add(new Paragraph(
                String.format(
                        "%-22s %10s",
                        "VAT (8%)",
                        MoneyUtils.formatVnd(vat)
                ),
                normalFont
        ));

        document.add(new Paragraph(
                String.format(
                        "%-22s %10s",
                        "Phi dich vu (15%)",
                        MoneyUtils.formatVnd(svc)
                ),
                normalFont
        ));

        document.add(new Paragraph(
                "----------------------------------",
                normalFont
        ));

        document.add(new Paragraph(
                String.format(
                        "%-22s %10s",
                        "Tong cong",
                        MoneyUtils.formatVnd(total)
                ),
                totalFont
        ));

        document.add(new Paragraph(" "));

        Paragraph thank =
                new Paragraph(
                        "Cam on quy khach!",
                        boldFont
                );

        thank.setAlignment(Element.ALIGN_CENTER);

        document.add(thank);

        document.close();
    }

    private static String safe(String s, int max) {

        if (s == null) {
            return "";
        }

        s = s.trim();

        if (s.length() <= max) {
            return s;
        }

        return s.substring(0, max - 1) + ".";
    }
}