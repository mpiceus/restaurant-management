package controller;

import java.util.List;
import model.ChiTietBanDTO;
import service.ChiTietBanService;
import service.ServiceException;

public class ChiTietBanController {
    private final ChiTietBanService chiTietBanService = new ChiTietBanService();

    public List<ChiTietBanDTO> getByBanAndUser(int banId, int userId) {
        return chiTietBanService.getByBanAndUser(banId, userId);
    }

    public Integer getServingUserId(int banId) {
        return chiTietBanService.getServingUserId(banId);
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

