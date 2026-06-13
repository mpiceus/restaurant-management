package util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
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
        Font titleFont = createUnicodeFont(15, Font.BOLD);
        Font normalFont = createUnicodeFont(9, Font.NORMAL);
        Font boldFont = createUnicodeFont(9, Font.BOLD);
        Font totalFont = createUnicodeFont(10, Font.BOLD);

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

        PdfPTable itemTable = new PdfPTable(new float[]{0.62f, 0.14f, 0.24f});
        itemTable.setWidthPercentage(100);
        itemTable.setSpacingBefore(2f);
        itemTable.setSpacingAfter(4f);

        itemTable.addCell(headerCell("Ten mon", boldFont, Element.ALIGN_LEFT));
        itemTable.addCell(headerCell("SL", boldFont, Element.ALIGN_CENTER));
        itemTable.addCell(headerCell("Thanh tien", boldFont, Element.ALIGN_RIGHT));

        for (InvoicePaperPanel.LineItem item : items) {
            BigDecimal thanhTien = item.donGia.multiply(BigDecimal.valueOf(item.soLuong));
            itemTable.addCell(bodyCell(safe(item.tenMon, 16), normalFont, Element.ALIGN_LEFT));
            itemTable.addCell(bodyCell(String.valueOf(item.soLuong), normalFont, Element.ALIGN_CENTER));
            itemTable.addCell(bodyCell(MoneyUtils.formatVnd(thanhTien), normalFont, Element.ALIGN_RIGHT));
        }

        document.add(itemTable);

        // TOTALS
        PdfPTable totalTable = new PdfPTable(new float[]{0.66f, 0.34f});
        totalTable.setWidthPercentage(100);
        totalTable.setSpacingBefore(2f);
        totalTable.setSpacingAfter(2f);

        totalTable.addCell(labelCell("Tong thanh tien", normalFont));
        totalTable.addCell(valueCell(MoneyUtils.formatVnd(tong), normalFont));
        totalTable.addCell(labelCell("VAT (8%)", normalFont));
        totalTable.addCell(valueCell(MoneyUtils.formatVnd(vat), normalFont));
        totalTable.addCell(labelCell("Phi dich vu (15%)", normalFont));
        totalTable.addCell(valueCell(MoneyUtils.formatVnd(svc), normalFont));
        totalTable.addCell(labelCell("Tong cong", totalFont));
        totalTable.addCell(valueCell(MoneyUtils.formatVnd(total), totalFont));

        document.add(totalTable);

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

    private static PdfPCell headerCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(3f);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private static PdfPCell bodyCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(2f);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private static PdfPCell labelCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(1f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    private static PdfPCell valueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(2f);
        cell.setPaddingBottom(1f);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
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

    private static Font createUnicodeFont(float size, int style) {
        try {
            BaseFont baseFont = BaseFont.createFont(
                    "C:/Windows/Fonts/arial.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            return new Font(baseFont, size, style);
        } catch (Exception e) {
            return FontFactory.getFont(FontFactory.HELVETICA, size, style);
        }
    }
}
