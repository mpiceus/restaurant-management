package service;

import dao.BanDAO;
import model.Ban;

import java.util.List;

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
            throw new ServiceException("TÃªn bÃ n khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng.");
        }
        try {
            banDAO.insert(new Ban(0, tenBan.trim(), trangThai));
        } catch (Exception e) {
            throw new ServiceException("KhÃ´ng thá»ƒ thÃªm bÃ n.", e);
        }
    }

    public void update(int banId, String tenBan, String trangThai) throws ServiceException {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new ServiceException("TÃªn bÃ n khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng.");
        }
        try {
            banDAO.update(new Ban(banId, tenBan.trim(), trangThai));
        } catch (Exception e) {
            throw new ServiceException("KhÃ´ng thá»ƒ cáº­p nháº­t bÃ n.", e);
        }
    }

    public void updateTrangThai(int banId, String trangThai) throws ServiceException {
        try {
            banDAO.updateTrangThai(banId, trangThai);
        } catch (Exception e) {
            throw new ServiceException("KhÃ´ng thá»ƒ cáº­p nháº­t tráº¡ng thÃ¡i bÃ n.", e);
        }
    }

    public void delete(int banId) throws ServiceException {
        try {
            banDAO.delete(banId);
        } catch (Exception e) {
            throw new ServiceException("KhÃ´ng thá»ƒ xÃ³a bÃ n (cÃ³ thá»ƒ Ä‘ang Ä‘Æ°á»£c tham chiáº¿u bá»Ÿi hÃ³a Ä‘Æ¡n).", e);
        }
    }
}

