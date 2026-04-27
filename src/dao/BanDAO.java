package dao;

import model.Ban;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BanDAO {

    public List<Ban> findAll() {
        String sql = "SELECT ban_id, ten_ban, trang_thai FROM Ban ORDER BY ban_id DESC";
        List<Ban> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Ban(rs.getInt("ban_id"), rs.getString("ten_ban"), rs.getString("trang_thai")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(Ban ban) throws Exception {
        String sql = "INSERT INTO Ban(ten_ban, trang_thai) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ban.getTenBan());
            ps.setString(2, ban.getTrangThai() == null ? "TRONG" : ban.getTrangThai());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new Exception("Không lấy được ban_id sau khi insert Ban.");
    }

    public void update(Ban ban) throws Exception {
        String sql = "UPDATE Ban SET ten_ban = ?, trang_thai = ? WHERE ban_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ban.getTenBan());
            ps.setString(2, ban.getTrangThai());
            ps.setInt(3, ban.getBanId());
            ps.executeUpdate();
        }
    }

    public void updateTrangThai(int banId, String trangThai) throws Exception {
        String sql = "UPDATE Ban SET trang_thai = ? WHERE ban_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai);
            ps.setInt(2, banId);
            ps.executeUpdate();
        }
    }

    public void delete(int banId) throws Exception {
        String sql = "DELETE FROM Ban WHERE ban_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, banId);
            ps.executeUpdate();
        }
    }
}

