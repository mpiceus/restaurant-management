package service;

import dao.BanDAO;
import java.util.List;
import model.Ban;

public class BanService {
    private final BanDAO banDAO = new BanDAO();

    public List<Ban> getAll() {
        return banDAO.findAll();
    }

    public Ban getById(int banId) {
        return banDAO.findById(banId);
    }

    public void create(String tenBan, String trangThai) throws ServiceException {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new ServiceException("Tên bàn không được phép để trống");
        }
        try {
            banDAO.insert(new Ban(0, tenBan.trim(), trangThai));
        } catch (Exception e) {
            throw new ServiceException("Không thể thêm bàn.", e);
        }
    }

    public void update(int banId, String tenBan, String trangThai) throws ServiceException {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new ServiceException("Tên bàn không được phép để trống");
        }
        try {
            banDAO.update(new Ban(banId, tenBan.trim(), trangThai));
        } catch (Exception e) {
            throw new ServiceException("Không thể sửa bàn.", e);
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
            throw new ServiceException("Không thể xóa bàn do đang được tham chiếu từ bảng khác", e);
        }
    }
}

