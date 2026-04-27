package service;

import dao.ChiTietBanDAO;
import model.ChiTietBanDTO;

import java.util.List;

public class ChiTietBanService {
    private final ChiTietBanDAO chiTietBanDAO = new ChiTietBanDAO();

    public List<ChiTietBanDTO> getByBanAndUser(int banId, int userId) {
        return chiTietBanDAO.findByBanAndUser(banId, userId);
    }

    public void addMon(int banId, int monId, int soLuong, int userId) throws ServiceException {
        if (soLuong <= 0) {
            throw new ServiceException("Số lượng phải > 0.");
        }
        try {
            chiTietBanDAO.insert(banId, monId, soLuong, userId);
        } catch (Exception e) {
            throw new ServiceException("Không thể gọi món.", e);
        }
    }

    public void updateSoLuong(int chiTietBanId, int soLuong) throws ServiceException {
        if (soLuong <= 0) {
            throw new ServiceException("Số lượng phải > 0.");
        }
        try {
            chiTietBanDAO.updateSoLuong(chiTietBanId, soLuong);
        } catch (Exception e) {
            throw new ServiceException("Không thể cập nhật số lượng.", e);
        }
    }

    public void delete(int chiTietBanId) throws ServiceException {
        try {
            chiTietBanDAO.delete(chiTietBanId);
        } catch (Exception e) {
            throw new ServiceException("Không thể xóa dòng order.", e);
        }
    }

    public boolean hasAnyItem(int banId, int userId) {
        return chiTietBanDAO.countByBanAndUser(banId, userId) > 0;
    }
}

