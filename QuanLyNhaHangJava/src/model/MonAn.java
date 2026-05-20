package model;

public class MonAn {
    private int monId;
    private String tenMon;
    private int loaiId;
    private String trangThai;
    private String hinhAnh;

    public MonAn() {
    }

    public MonAn(int monId, String tenMon, int loaiId, String trangThai, String hinhAnh) {
        this.monId = monId;
        this.tenMon = tenMon;
        this.loaiId = loaiId;
        this.trangThai = trangThai;
        this.hinhAnh = hinhAnh;
    }

    public int getMonId() { return monId; }
    public String getTenMon() { return tenMon; }
    public int getLoaiId() { return loaiId; }
    public String getTrangThai() { return trangThai; }
    public String getHinhAnh() { return hinhAnh;}

    public void setMonId(int monId) { this.monId = monId; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
    public void setLoaiId(int loaiId) { this.loaiId = loaiId; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh;}
}
