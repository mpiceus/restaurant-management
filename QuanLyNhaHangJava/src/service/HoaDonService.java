package service;

import dao.BangGiaDAO;
import dao.ChiTietBanDAO;
import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import model.HoaDonDTO;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 * Thanh toán: tạo hóa đơn + chi tiết hóa đơn theo transaction.
 */
public class HoaDonService {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private final ChiTietBanDAO chiTietBanDAO = new ChiTietBanDAO();
    private final BangGiaDAO bangGiaDAO = new BangGiaDAO();

    public int checkoutBan(int banId, int userId) throws ServiceException {
        // Nếu bàn chưa có món -> báo lỗi
        if (chiTietBanDAO.countByBanAndUser(banId, userId) == 0) {
            throw new ServiceException("Bàn chưa có món để thanh toán.");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1) Insert HoaDon (tong_tien = 0)
            int hoaDonId = hoaDonDAO.insertHoaDon(conn, banId, userId);

            // 2) Insert ChiTietHoaDon (lấy đơn giá mới nhất)
            BigDecimal tongTien = BigDecimal.ZERO;
            List<int[]> items = chiTietBanDAO.findForCheckout(banId, userId); // [mon_id, so_luong]
            for (int[] item : items) {
                int monId = item[0];
                int soLuong = item[1];

                BigDecimal donGia = bangGiaDAO.getLatestPrice(monId);
                if (donGia == null) {
                    throw new ServiceException("Món id=" + monId + " chưa có giá trong bảng BangGia.");
                }

                BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
                chiTietHoaDonDAO.insertChiTietHoaDon(conn, hoaDonId, monId, soLuong, donGia, thanhTien);
                tongTien = tongTien.add(thanhTien);
            }

            // 3) Update HoaDon.tong_tien
            hoaDonDAO.updateTongTien(conn, hoaDonId, tongTien);

            // Clear order tạm của nhân viên trên bàn
            chiTietBanDAO.deleteByBanAndUser(conn, banId, userId);

            conn.commit();
            return hoaDonId;
        } catch (ServiceException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (Exception e) {
            rollbackQuietly(conn);
            throw new ServiceException("Thanh toán thất bại.", e);
        } finally {
            closeQuietly(conn);
        }
    }

    public List<HoaDonDTO> getAllHoaDon() {
        return hoaDonDAO.findAllHoaDon();
    }

    public List<HoaDonDTO> getHoaDonByUser(int userId) {
        return hoaDonDAO.findHoaDonByUser(userId);
    }

    public List<Object[]> getHoaDonDetails(int hoaDonId) {
        return chiTietHoaDonDAO.findDetailsByHoaDonId(hoaDonId);
    }

    public HoaDonDTO getHoaDonById(int hoaDonId) {
        return hoaDonDAO.findById(hoaDonId);
    }

    private void rollbackQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (Exception ignored) {
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (Exception ignored) {
        }
    }
}
