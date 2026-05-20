package model;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private int cthdId;
    private int hoaDonId;
    private int monId;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(int cthdId, int hoaDonId, int monId, int soLuong, BigDecimal donGia, BigDecimal thanhTien) {
        this.cthdId = cthdId;
        this.hoaDonId = hoaDonId;
        this.monId = monId;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public int getCthdId() {
        return cthdId;
    }

    public void setCthdId(int cthdId) {
        this.cthdId = cthdId;
    }

    public int getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(int hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public int getMonId() {
        return monId;
    }

    public void setMonId(int monId) {
        this.monId = monId;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public BigDecimal getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(BigDecimal thanhTien) {
        this.thanhTien = thanhTien;
    }
}

