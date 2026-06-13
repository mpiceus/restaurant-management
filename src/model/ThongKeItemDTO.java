package model;

public class ThongKeItemDTO {
    private Integer id;
    private String ten;
    private int soLuong;

    public ThongKeItemDTO(Integer id, String ten, int soLuong) {
        this.id = id;
        this.ten = ten;
        this.soLuong = soLuong;
    }

    public Integer getId() {
        return id;
    }

    public String getTen() {
        return ten;
    }

    public int getSoLuong() {
        return soLuong;
    }
}
