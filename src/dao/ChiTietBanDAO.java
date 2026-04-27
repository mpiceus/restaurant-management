package dao;

import model.ChiTietBanDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ChiTietBanDAO {

    public List<ChiTietBanDTO> findByBanAndUser(int banId, int userId) {
        String sql =
                "SELECT c.chitietban_id, c.mon_id, m.ten_mon, c.so_luong, c.thoi_gian " +
                        "FROM ChiTietBan c " +
                        "JOIN MonAn m ON c.mon_id = m.mon_id " +
                        "WHERE c.ban_id = ? AND c.user_id = ? " +
                        "ORDER BY c.thoi_gian DESC, c.chitietban_id DESC";

        List<ChiTietBanDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, banId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp t = rs.getTimestamp("thoi_gian");
                    list.add(new ChiTietBanDTO(
                            rs.getInt("chitietban_id"),
                            rs.getInt("mon_id"),
                            rs.getString("ten_mon"),
                            rs.getInt("so_luong"),
                            t == null ? null : t.toLocalDateTime()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(int banId, int monId, int soLuong, int userId) throws Exception {
        String sql = "INSERT INTO ChiTietBan(ban_id, mon_id, so_luong, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, banId);
            ps.setInt(2, monId);
            ps.setInt(3, soLuong);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public void updateSoLuong(int chiTietBanId, int soLuong) throws Exception {
        String sql = "UPDATE ChiTietBan SET so_luong = ? WHERE chitietban_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setInt(2, chiTietBanId);
            ps.executeUpdate();
        }
    }

    public void delete(int chiTietBanId) throws Exception {
        String sql = "DELETE FROM ChiTietBan WHERE chitietban_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, chiTietBanId);
            ps.executeUpdate();
        }
    }

    public int countByBanAndUser(int banId, int userId) {
        String sql = "SELECT COUNT(*) AS c FROM ChiTietBan WHERE ban_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, banId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Dữ liệu tối thiểu để thanh toán: mon_id + so_luong của nhân viên trên bàn.
     */
    public List<int[]> findForCheckout(int banId, int userId) {
        String sql = "SELECT mon_id, so_luong FROM ChiTietBan WHERE ban_id = ? AND user_id = ?";
        List<int[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, banId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new int[]{rs.getInt("mon_id"), rs.getInt("so_luong")});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteByBanAndUser(Connection conn, int banId, int userId) throws Exception {
        String sql = "DELETE FROM ChiTietBan WHERE ban_id = ? AND user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, banId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}
