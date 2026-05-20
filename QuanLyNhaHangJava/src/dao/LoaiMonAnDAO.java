package dao;

import model.LoaiMonAn;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoaiMonAnDAO {

    public List<LoaiMonAn> findAll() {
        String sql = "SELECT loai_id, ten_loai FROM LoaiMonAn ORDER BY loai_id DESC";
        List<LoaiMonAn> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new LoaiMonAn(rs.getInt("loai_id"), rs.getString("ten_loai")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public LoaiMonAn findById(int loaiId) {
        String sql = "SELECT loai_id, ten_loai FROM LoaiMonAn WHERE loai_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loaiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LoaiMonAn(rs.getInt("loai_id"), rs.getString("ten_loai"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(LoaiMonAn loai) throws Exception {
        String sql = "INSERT INTO LoaiMonAn(ten_loai) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, loai.getTenLoai());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new Exception("Không lấy được loai_id sau khi insert LoaiMonAn.");
    }

    public void update(LoaiMonAn loai) throws Exception {
        String sql = "UPDATE LoaiMonAn SET ten_loai = ? WHERE loai_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loai.getTenLoai());
            ps.setInt(2, loai.getLoaiId());
            ps.executeUpdate();
        }
    }

    public void delete(int loaiId) throws Exception {
        String sql = "DELETE FROM LoaiMonAn WHERE loai_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loaiId);
            ps.executeUpdate();
        }
    }
}

