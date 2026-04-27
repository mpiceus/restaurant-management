package model;

import java.math.BigDecimal;

/**
 * DTO hiển thị món ăn kèm tên loại và đơn giá mới nhất.
 */
public class MonAnWithPriceDTO {
    private int monId;
    private String tenMon;
    private int loaiId;
    private String tenLoai;
    private String trangThai;
    private BigDecimal gia;

    public MonAnWithPriceDTO(int monId, String tenMon, int loaiId, String tenLoai, String trangThai, BigDecimal gia) {
        this.monId = monId;
        this.tenMon = tenMon;
        this.loaiId = loaiId;
        this.tenLoai = tenLoai;
        this.trangThai = trangThai;
        this.gia = gia;
    }

    public int getMonId() {
        return monId;
    }

    public String getTenMon() {
        return tenMon;
    }

    public int getLoaiId() {
        return loaiId;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public BigDecimal getGia() {
        return gia;
    }
}

