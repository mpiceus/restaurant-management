package model;

import java.time.LocalDateTime;

public class ChiTietBan {
    private int chiTietBanId;
    private int banId;
    private int monId;
    private int soLuong;
    private int userId;
    private LocalDateTime thoiGian;

    public ChiTietBan() {
    }

    public ChiTietBan(int chiTietBanId, int banId, int monId, int soLuong, int userId, LocalDateTime thoiGian) {
        this.chiTietBanId = chiTietBanId;
        this.banId = banId;
        this.monId = monId;
        this.soLuong = soLuong;
        this.userId = userId;
        this.thoiGian = thoiGian;
    }

    public int getChiTietBanId() {
        return chiTietBanId;
    }

    public void setChiTietBanId(int chiTietBanId) {
        this.chiTietBanId = chiTietBanId;
    }

    public int getBanId() {
        return banId;
    }

    public void setBanId(int banId) {
        this.banId = banId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }
}

