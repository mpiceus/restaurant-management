package service;

import dao.BangGiaDAO;
import model.BangGia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BangGiaService {
    private final BangGiaDAO bangGiaDAO = new BangGiaDAO();

    public List<BangGia> getAll() {
        return bangGiaDAO.findAll();
    }

    public int create(int monId, BigDecimal gia, LocalDate ngayApDung) throws ServiceException {
        if (gia == null || gia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Giá phải > 0.");
        }
        try {
            return bangGiaDAO.insert(new BangGia(0, monId, gia, ngayApDung));
        } catch (Exception e) {
            throw new ServiceException("Không thể thêm bảng giá.", e);
        }
    }

    public void update(int bangGiaId, int monId, BigDecimal gia, LocalDate ngayApDung) throws ServiceException {
        if (gia == null || gia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Giá phải > 0.");
        }
        if (ngayApDung == null) {
            throw new ServiceException("Ngày áp dụng không hợp lệ.");
        }
        try {
            bangGiaDAO.update(new BangGia(bangGiaId, monId, gia, ngayApDung));
        } catch (Exception e) {
            throw new ServiceException("Không thể cập nhật bảng giá.", e);
        }
    }

    public void delete(int bangGiaId) throws ServiceException {
        try {
            bangGiaDAO.delete(bangGiaId);
        } catch (Exception e) {
            throw new ServiceException("Không thể xóa bảng giá.", e);
        }
    }
}

