package service;

import dao.BangGiaDAO;
import dao.MonAnDAO;
import model.MonAn;
import model.MonAnWithPriceDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MonAnService {
    private final MonAnDAO monAnDAO = new MonAnDAO();
    private final BangGiaDAO bangGiaDAO = new BangGiaDAO();

    public List<MonAnWithPriceDTO> getAllWithLatestPrice() {
        return monAnDAO.findAllWithLatestPrice();
    }

    public List<MonAnWithPriceDTO> getByLoai(int loaiId) {
        return monAnDAO.findByLoaiIdWithLatestPrice(loaiId);
    }

    public List<MonAnWithPriceDTO> search(String keyword, Integer loaiId, Integer monId) {
        return monAnDAO.searchWithLatestPrice(keyword, loaiId, monId);
    }

    /**
     * Thêm món + thêm giá (BangGia) theo ngày hiện tại.
     */
    public void create(String tenMon, int loaiId, String trangThai, BigDecimal gia) throws ServiceException {
        if (tenMon == null || tenMon.trim().isEmpty()) {
            throw new ServiceException("Tên món không được để trống.");
        }
        if (gia == null || gia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Giá phải > 0.");
        }

        try {
            MonAn mon = new MonAn();
            mon.setTenMon(tenMon.trim());
            mon.setLoaiId(loaiId);
            mon.setTrangThai(trangThai == null || trangThai.trim().isEmpty() ? "CON" : trangThai.trim());

            int monId = monAnDAO.insert(mon);
            bangGiaDAO.insert(new model.BangGia(0, monId, gia, LocalDate.now()));
        } catch (Exception e) {
            throw new ServiceException("Không thể thêm món ăn.", e);
        }
    }

    /**
     * Cập nhật thông tin món. Nếu muốn thay giá, insert thêm dòng BangGia mới.
     */
    public void update(int monId, String tenMon, int loaiId, String trangThai, BigDecimal giaMoiOrNull)
            throws ServiceException {
        if (tenMon == null || tenMon.trim().isEmpty()) {
            throw new ServiceException("Tên món không được để trống.");
        }
        try {
            MonAn mon = new MonAn();
            mon.setMonId(monId);
            mon.setTenMon(tenMon.trim());
            mon.setLoaiId(loaiId);
            mon.setTrangThai(trangThai == null || trangThai.trim().isEmpty() ? "CON" : trangThai.trim());
            monAnDAO.update(mon);

            if (giaMoiOrNull != null) {
                if (giaMoiOrNull.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ServiceException("Giá phải > 0.");
                }
                bangGiaDAO.insert(new model.BangGia(0, monId, giaMoiOrNull, LocalDate.now()));
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Không thể cập nhật món ăn.", e);
        }
    }

    public void delete(int monId) throws ServiceException {
        try {
            // Nếu DB có FK từ BangGia -> MonAn, cần xóa BangGia trước (hoặc ON DELETE CASCADE).
            // Ở đây ưu tiên dễ hiểu: báo lỗi nếu không xóa được.
            monAnDAO.delete(monId);
        } catch (Exception e) {
            throw new ServiceException("Không thể xóa món (có thể đang có bảng giá / hóa đơn tham chiếu).", e);
        }
    }
}

