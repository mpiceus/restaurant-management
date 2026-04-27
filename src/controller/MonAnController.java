package controller;

import model.MonAnWithPriceDTO;
import service.MonAnService;
import service.ServiceException;

import java.math.BigDecimal;
import java.util.List;

public class MonAnController {
    private final MonAnService monAnService = new MonAnService();

    public List<MonAnWithPriceDTO> getAll() {
        return monAnService.getAllWithLatestPrice();
    }

    public List<MonAnWithPriceDTO> search(String keyword, Integer loaiId, Integer monId) {
        return monAnService.search(keyword, loaiId, monId);
    }

    public void create(String tenMon, int loaiId, String trangThai, BigDecimal gia) throws ServiceException {
        monAnService.create(tenMon, loaiId, trangThai, gia);
    }

    public void update(int monId, String tenMon, int loaiId, String trangThai, BigDecimal giaMoiOrNull)
            throws ServiceException {
        monAnService.update(monId, tenMon, loaiId, trangThai, giaMoiOrNull);
    }

    public void delete(int monId) throws ServiceException {
        monAnService.delete(monId);
    }
}

