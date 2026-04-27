package controller;

import model.HoaDonDTO;
import service.HoaDonService;
import service.ServiceException;

import java.util.List;

public class HoaDonController {
    private final HoaDonService hoaDonService = new HoaDonService();

    public int checkout(int banId, int userId) throws ServiceException {
        return hoaDonService.checkoutBan(banId, userId);
    }

    public List<HoaDonDTO> getAll() {
        return hoaDonService.getAllHoaDon();
    }

    public List<HoaDonDTO> getByUser(int userId) {
        return hoaDonService.getHoaDonByUser(userId);
    }

    public List<Object[]> getDetails(int hoaDonId) {
        return hoaDonService.getHoaDonDetails(hoaDonId);
    }
}
