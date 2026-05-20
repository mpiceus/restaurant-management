package controller;

import model.BangGia;
import service.BangGiaService;
import service.ServiceException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BangGiaController {
    private final BangGiaService bangGiaService = new BangGiaService();

    public List<BangGia> getAll() {
        return bangGiaService.getAll();
    }

    public void create(int monId, BigDecimal gia, LocalDate ngayApDung) throws ServiceException {
        bangGiaService.create(monId, gia, ngayApDung);
    }

    public void update(int bangGiaId, int monId, BigDecimal gia, LocalDate ngayApDung) throws ServiceException {
        bangGiaService.update(bangGiaId, monId, gia, ngayApDung);
    }

    public void delete(int bangGiaId) throws ServiceException {
        bangGiaService.delete(bangGiaId);
    }
}

