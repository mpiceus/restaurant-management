package model;

public class Ban {
    private int banId;
    private String tenBan;
    private String trangThai;

    public Ban() {
    }

    public Ban(int banId, String tenBan, String trangThai) {
        this.banId = banId;
        this.tenBan = tenBan;
        this.trangThai = trangThai;
    }

    public int getBanId() {
        return banId;
    }

    public void setBanId(int banId) {
        this.banId = banId;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}

