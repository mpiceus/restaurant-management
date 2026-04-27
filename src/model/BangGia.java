package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BangGia {
    private int bangGiaId;
    private int monId;
    private BigDecimal gia;
    private LocalDate ngayApDung;

    public BangGia() {
    }

    public BangGia(int bangGiaId, int monId, BigDecimal gia, LocalDate ngayApDung) {
        this.bangGiaId = bangGiaId;
        this.monId = monId;
        this.gia = gia;
        this.ngayApDung = ngayApDung;
    }

    public int getBangGiaId() {
        return bangGiaId;
    }

    public void setBangGiaId(int bangGiaId) {
        this.bangGiaId = bangGiaId;
    }

    public int getMonId() {
        return monId;
    }

    public void setMonId(int monId) {
        this.monId = monId;
    }

    public BigDecimal getGia() {
        return gia;
    }

    public void setGia(BigDecimal gia) {
        this.gia = gia;
    }

    public LocalDate getNgayApDung() {
        return ngayApDung;
    }

    public void setNgayApDung(LocalDate ngayApDung) {
        this.ngayApDung = ngayApDung;
    }
}

