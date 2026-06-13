package controller;

import java.time.LocalDate;
import model.BaoCaoThongKeDTO;
import service.ThongKeService;

public class ThongKeController {
    private final ThongKeService thongKeService = new ThongKeService();

    public BaoCaoThongKeDTO getBaoCao(LocalDate from, LocalDate to) {
        return thongKeService.getBaoCao(from, to);
    }
}
