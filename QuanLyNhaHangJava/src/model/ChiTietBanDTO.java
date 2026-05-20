package model;

import java.time.LocalDateTime;

/**
 * DTO hiển thị order tạm theo bàn.
 */
public class ChiTietBanDTO {
    private int chiTietBanId;
    private int monId;
    private String tenMon;
    private int soLuong;
    private LocalDateTime thoiGian;

    public ChiTietBanDTO(int chiTietBanId, int monId, String tenMon, int soLuong, LocalDateTime thoiGian) {
        this.chiTietBanId = chiTietBanId;
        this.monId = monId;
        this.tenMon = tenMon;
        this.soLuong = soLuong;
        this.thoiGian = thoiGian;
    }

    public int getChiTietBanId() {
        return chiTietBanId;
    }

    public int getMonId() {
        return monId;
    }

    public String getTenMon() {
        return tenMon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }
}

