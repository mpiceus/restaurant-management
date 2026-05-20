package dao;

import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    public void insertChiTietHoaDon(Connection conn, int hoaDonId, int monId, int soLuong, BigDecimal donGia,
                                   BigDecimal thanhTien) throws Exception {
        String sql =
                "INSERT INTO ChiTietHoaDon(hoadon_id, mon_id, so_luong, don_gia, thanh_tien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            ps.setInt(2, monId);
            ps.setInt(3, soLuong);
            ps.setBigDecimal(4, donGia);
            ps.setBigDecimal(5, thanhTien);
            ps.executeUpdate();
        }
    }

    public List<Object[]> findDetailsByHoaDonId(int hoaDonId) {
        // trả về mảng: ten_mon, so_luong, don_gia, thanh_tien
        String sql =
                "SELECT m.ten_mon, c.so_luong, c.don_gia, c.thanh_tien " +
                        "FROM ChiTietHoaDon c " +
                        "JOIN MonAn m ON c.mon_id = m.mon_id " +
                        "WHERE c.hoadon_id = ? " +
                        "ORDER BY c.cthd_id ASC";

        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("ten_mon"),
                            rs.getInt("so_luong"),
                            rs.getBigDecimal("don_gia"),
                            rs.getBigDecimal("thanh_tien")
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

