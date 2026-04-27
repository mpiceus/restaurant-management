package controller;

import model.LoaiMonAn;
import service.LoaiMonAnService;
import service.ServiceException;

import java.util.List;

public class LoaiMonAnController {
    private final LoaiMonAnService loaiService = new LoaiMonAnService();

    public List<LoaiMonAn> getAll() {
        return loaiService.getAll();
    }

    public void create(String tenLoai) throws ServiceException {
        loaiService.create(tenLoai);
    }

    public void update(int loaiId, String tenLoai) throws ServiceException {
        loaiService.update(loaiId, tenLoai);
    }

    public void delete(int loaiId) throws ServiceException {
        loaiService.delete(loaiId);
    }
}

