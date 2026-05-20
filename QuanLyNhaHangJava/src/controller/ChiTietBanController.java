package controller;

import model.ChiTietBanDTO;
import service.ChiTietBanService;
import service.ServiceException;

import java.util.List;

public class ChiTietBanController {
    private final ChiTietBanService chiTietBanService = new ChiTietBanService();

    public List<ChiTietBanDTO> getByBanAndUser(int banId, int userId) {
        return chiTietBanService.getByBanAndUser(banId, userId);
    }

    public void addMon(int banId, int monId, int soLuong, int userId) throws ServiceException {
        chiTietBanService.addMon(banId, monId, soLuong, userId);
    }

    public void updateSoLuong(int chiTietBanId, int soLuong) throws ServiceException {
        chiTietBanService.updateSoLuong(chiTietBanId, soLuong);
    }

    public void delete(int chiTietBanId) throws ServiceException {
        chiTietBanService.delete(chiTietBanId);
    }
}

