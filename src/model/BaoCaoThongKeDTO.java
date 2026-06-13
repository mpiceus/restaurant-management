package model;

import java.math.BigDecimal;
import java.util.List;

public class BaoCaoThongKeDTO {
    private int tongHoaDon;
    private BigDecimal tongDoanhThu;
    private List<ThongKeItemDTO> topMonBanChay;
    private List<ThongKeItemDTO> topNhanVien;
    private List<ThongKeItemDTO> topBan;
    private List<HourlyTrendDTO> hourlyTrend;

    public BaoCaoThongKeDTO(int tongHoaDon, BigDecimal tongDoanhThu,
                             List<ThongKeItemDTO> topMonBanChay,
                             List<ThongKeItemDTO> topNhanVien,
                             List<ThongKeItemDTO> topBan,
                             List<HourlyTrendDTO> hourlyTrend) {
        this.tongHoaDon = tongHoaDon;
        this.tongDoanhThu = tongDoanhThu;
        this.topMonBanChay = topMonBanChay;
        this.topNhanVien = topNhanVien;
        this.topBan = topBan;
        this.hourlyTrend = hourlyTrend;
    }

    public int getTongHoaDon() {
        return tongHoaDon;
    }

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }

    public List<ThongKeItemDTO> getTopMonBanChay() {
        return topMonBanChay;
    }

    public List<ThongKeItemDTO> getTopNhanVien() {
        return topNhanVien;
    }

    public List<ThongKeItemDTO> getTopBan() {
        return topBan;
    }

    public List<HourlyTrendDTO> getHourlyTrend() {
        return hourlyTrend;
    }
}
