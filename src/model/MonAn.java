package model;

public class MonAn {
    private int monId;
    private String tenMon;
    private int loaiId;
    private String trangThai;

    public MonAn() {
    }

    public MonAn(int monId, String tenMon, int loaiId, String trangThai) {
        this.monId = monId;
        this.tenMon = tenMon;
        this.loaiId = loaiId;
        this.trangThai = trangThai;
    }

    public int getMonId() { return monId; }
    public String getTenMon() { return tenMon; }
    public int getLoaiId() { return loaiId; }
    public String getTrangThai() { return trangThai; }

    public void setMonId(int monId) { this.monId = monId; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    public void setLoaiId(int loaiId) { this.loaiId = loaiId; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
