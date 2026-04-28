package dao;

import model.HoaDonDTO;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public int insertHoaDon(Connection conn, int banId, int userId) throws Exception {
        String sql = "INSERT INTO HoaDon(ban_id, user_id, tong_tien) VALUES (?, ?, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, banId);
            ps.setInt(2, userId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new Exception("Không lấy được hoadon_id sau khi insert HoaDon.");
    }

    public void updateTongTien(Connection conn, int hoaDonId, BigDecimal tongTien) throws Exception {
        String sql = "UPDATE HoaDon SET tong_tien = ? WHERE hoadon_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, tongTien);
            ps.setInt(2, hoaDonId);
            ps.executeUpdate();
        }
    }

    public List<HoaDonDTO> findAllHoaDon() {
        String sql =
                "SELECT h.hoadon_id, h.ban_id, b.ten_ban, h.user_id, COALESCE(u.fullname, u.username) AS ten_nv, h.tong_tien, h.ngay_tao " +
                        "FROM HoaDon h " +
                        "JOIN Ban b ON h.ban_id = b.ban_id " +
                        "JOIN Users u ON h.user_id = u.user_id " +
                        "ORDER BY h.ngay_tao DESC, h.hoadon_id DESC";

        List<HoaDonDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp t = rs.getTimestamp("ngay_tao");
                list.add(new HoaDonDTO(
                        rs.getInt("hoadon_id"),
                        rs.getInt("ban_id"),
                        rs.getString("ten_ban"),
                        rs.getInt("user_id"),
                        rs.getString("ten_nv"),
                        rs.getBigDecimal("tong_tien"),
                        t == null ? null : t.toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<HoaDonDTO> findHoaDonByUser(int userId) {
        String sql =
                "SELECT h.hoadon_id, h.ban_id, b.ten_ban, h.user_id, COALESCE(u.fullname, u.username) AS ten_nv, h.tong_tien, h.ngay_tao " +
                        "FROM HoaDon h " +
                        "JOIN Ban b ON h.ban_id = b.ban_id " +
                        "JOIN Users u ON h.user_id = u.user_id " +
                        "WHERE h.user_id = ? " +
                        "ORDER BY h.ngay_tao DESC, h.hoadon_id DESC";

        List<HoaDonDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp t = rs.getTimestamp("ngay_tao");
                    list.add(new HoaDonDTO(
                            rs.getInt("hoadon_id"),
                            rs.getInt("ban_id"),
                            rs.getString("ten_ban"),
                            rs.getInt("user_id"),
                            rs.getString("ten_nv"),
                            rs.getBigDecimal("tong_tien"),
                            t == null ? null : t.toLocalDateTime()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public HoaDonDTO findById(int hoaDonId) {
        String sql =
                "SELECT h.hoadon_id, h.ban_id, b.ten_ban, h.user_id, COALESCE(u.fullname, u.username) AS ten_nv, h.tong_tien, h.ngay_tao " +
                        "FROM HoaDon h " +
                        "JOIN Ban b ON h.ban_id = b.ban_id " +
                        "JOIN Users u ON h.user_id = u.user_id " +
                        "WHERE h.hoadon_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, hoaDonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp t = rs.getTimestamp("ngay_tao");
                    return new HoaDonDTO(
                            rs.getInt("hoadon_id"),
                            rs.getInt("ban_id"),
                            rs.getString("ten_ban"),
                            rs.getInt("user_id"),
                            rs.getString("ten_nv"),
                            rs.getBigDecimal("tong_tien"),
                            t == null ? null : t.toLocalDateTime()
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
