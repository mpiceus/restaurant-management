package service;

import dao.ThongKeDAO;
import java.time.LocalDate;
import model.BaoCaoThongKeDTO;

public class ThongKeService {
    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public BaoCaoThongKeDTO getBaoCao(LocalDate from, LocalDate to) {
        return new BaoCaoThongKeDTO(
                thongKeDAO.countHoaDon(from, to),
                thongKeDAO.sumDoanhThu(from, to),
                thongKeDAO.topMonBanChay(from, to, 5),
                thongKeDAO.topNhanVien(from, to, 3),
                thongKeDAO.topBan(from, to, 3),
                thongKeDAO.hourlyTrend(from, to)
        );
    }
}
