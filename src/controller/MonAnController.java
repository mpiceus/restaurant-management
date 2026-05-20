package controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import model.MonAnWithPriceDTO;
import service.MonAnService;
import service.ServiceException;

public class MonAnController {
    private final MonAnService monAnService = new MonAnService();

    public List<MonAnWithPriceDTO> getAll() {
        return monAnService.getAllWithLatestPrice();
    }

    public List<MonAnWithPriceDTO> search(String keyword, Integer loaiId, Integer monId) {
        return monAnService.search(keyword, loaiId, monId);
    }

    public void create(String tenMon, int loaiId, String trangThai, BigDecimal gia, File hinhanh) throws ServiceException {
        monAnService.create(tenMon, loaiId, trangThai, gia, hinhanh);
    }

    public void update(int monId, String tenMon, int loaiId, String trangThai, BigDecimal giaMoiOrNull, File hinhAnh) throws ServiceException {
        monAnService.update(
                monId,
                tenMon,
                loaiId,
                trangThai,
                giaMoiOrNull,
                hinhAnh
        );
    }

    public void delete(int monId) throws ServiceException {
        monAnService.delete(monId);
    }
}

