package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO hiển thị hóa đơn.
 */
public class HoaDonDTO {
    private int hoaDonId;
    private int banId;
    private String tenBan;
    private int userId;
    private String tenNhanVien;
    private BigDecimal tongTien;
    private LocalDateTime ngayTao;

    public HoaDonDTO(int hoaDonId, int banId, String tenBan, int userId, String tenNhanVien, BigDecimal tongTien,
                     LocalDateTime ngayTao) {
        this.hoaDonId = hoaDonId;
        this.banId = banId;
        this.tenBan = tenBan;
        this.userId = userId;
        this.tenNhanVien = tenNhanVien;
        this.tongTien = tongTien;
        this.ngayTao = ngayTao;
    }

    public int getHoaDonId() {
        return hoaDonId;
    }

    public int getBanId() {
        return banId;
    }

    public String getTenBan() {
        return tenBan;
    }

    public int getUserId() {
        return userId;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }
}

