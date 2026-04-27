package service;

import dao.LoaiMonAnDAO;
import model.LoaiMonAn;

import java.util.List;

public class LoaiMonAnService {
    private final LoaiMonAnDAO loaiDAO = new LoaiMonAnDAO();

    public List<LoaiMonAn> getAll() {
        return loaiDAO.findAll();
    }

    public int create(String tenLoai) throws ServiceException {
        if (tenLoai == null || tenLoai.trim().isEmpty()) {
            throw new ServiceException("Tên loại không được để trống.");
        }
        try {
            return loaiDAO.insert(new LoaiMonAn(0, tenLoai.trim()));
        } catch (Exception e) {
            throw new ServiceException("Không thể thêm loại món.", e);
        }
    }

    public void update(int loaiId, String tenLoai) throws ServiceException {
        if (tenLoai == null || tenLoai.trim().isEmpty()) {
            throw new ServiceException("Tên loại không được để trống.");
        }
        try {
            loaiDAO.update(new LoaiMonAn(loaiId, tenLoai.trim()));
        } catch (Exception e) {
            throw new ServiceException("Không thể cập nhật loại món.", e);
        }
    }

    public void delete(int loaiId) throws ServiceException {
        try {
            loaiDAO.delete(loaiId);
        } catch (Exception e) {
            throw new ServiceException("Không thể xóa loại món (có thể đang được tham chiếu bởi món ăn).", e);
        }
    }
}

