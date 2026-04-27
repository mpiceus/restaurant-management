package service;

import dao.BanDAO;
import model.Ban;

import java.util.List;

public class BanService {
    private final BanDAO banDAO = new BanDAO();

    public List<Ban> getAll() {
        return banDAO.findAll();
    }

    public void create(String tenBan, String trangThai) throws ServiceException {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new ServiceException("Tên bàn không được để trống.");
        }
        try {
            banDAO.insert(new Ban(0, tenBan.trim(), trangThai));
        } catch (Exception e) {
            throw new ServiceException("Không thể thêm bàn.", e);
        }
    }

    public void update(int banId, String tenBan, String trangThai) throws ServiceException {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new ServiceException("Tên bàn không được để trống.");
        }
        try {
            banDAO.update(new Ban(banId, tenBan.trim(), trangThai));
        } catch (Exception e) {
            throw new ServiceException("Không thể cập nhật bàn.", e);
        }
    }

    public void updateTrangThai(int banId, String trangThai) throws ServiceException {
        try {
            banDAO.updateTrangThai(banId, trangThai);
        } catch (Exception e) {
            throw new ServiceException("Không thể cập nhật trạng thái bàn.", e);
        }
    }

    public void delete(int banId) throws ServiceException {
        try {
            banDAO.delete(banId);
        } catch (Exception e) {
            throw new ServiceException("Không thể xóa bàn (có thể đang được tham chiếu bởi hóa đơn).", e);
        }
    }
}

