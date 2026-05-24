package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDon {
    private int hoaDonId;
    private int banId;
    private int userId;
    private BigDecimal tongTien;
    private LocalDateTime ngayTao;
    private String filePdf;

    public HoaDon() {
    }

    public HoaDon(int hoaDonId, int banId, int userId, BigDecimal tongTien, LocalDateTime ngayTao, String filePdf) {
        this.hoaDonId = hoaDonId;
        this.banId = banId;
        this.userId = userId;
        this.tongTien = tongTien;
        this.ngayTao = ngayTao;
        this.filePdf = filePdf;
    }

    public int getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(int hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public int getBanId() {
        return banId;
    }

    public void setBanId(int banId) {
        this.banId = banId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getFilePdf() {
        return filePdf;
    }

    public void setFilePdf(String filePdf) {
        this.filePdf = filePdf;
    }
}

