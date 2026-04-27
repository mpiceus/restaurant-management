package controller;

import model.Ban;
import service.BanService;
import service.ServiceException;

import java.util.List;

public class BanController {
    private final BanService banService = new BanService();

    public List<Ban> getAll() {
        return banService.getAll();
    }

    public void create(String tenBan, String trangThai) throws ServiceException {
        banService.create(tenBan, trangThai);
    }

    public void update(int banId, String tenBan, String trangThai) throws ServiceException {
        banService.update(banId, tenBan, trangThai);
    }

    public void updateTrangThai(int banId, String trangThai) throws ServiceException {
        banService.updateTrangThai(banId, trangThai);
    }

    public void delete(int banId) throws ServiceException {
        banService.delete(banId);
    }
}

