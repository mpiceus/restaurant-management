package controller;

import java.util.List;
import model.HoaDonDTO;
import service.HoaDonService;
import service.ServiceException;

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

    public HoaDonDTO getById(int hoaDonId) {
        return hoaDonService.getHoaDonById(hoaDonId);
    }

    public void updateFilePdf(int hoaDonId, String path) 
            throws ServiceException {
        hoaDonService.updateFilePdf(hoaDonId, path);
    }
}
