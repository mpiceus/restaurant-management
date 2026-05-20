package model;

public class LoaiMonAn {
    private int loaiId;
    private String tenLoai;

    public LoaiMonAn() {
    }

    public LoaiMonAn(int loaiId, String tenLoai) {
        this.loaiId = loaiId;
        this.tenLoai = tenLoai;
    }

    public int getLoaiId() {
        return loaiId;
    }

    public void setLoaiId(int loaiId) {
        this.loaiId = loaiId;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }
}

