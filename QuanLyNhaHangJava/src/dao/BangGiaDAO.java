package dao;

import model.BangGia;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BangGiaDAO {

    public List<BangGia> findAll() {
        String sql = "SELECT banggia_id, mon_id, gia, ngay_ap_dung FROM BangGia ORDER BY ngay_ap_dung DESC, banggia_id DESC";
        List<BangGia> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public BigDecimal getLatestPrice(int monId) throws Exception {
        String sql = "SELECT TOP 1 gia FROM BangGia WHERE mon_id = ? ORDER BY ngay_ap_dung DESC, banggia_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, monId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("gia");
                }
            }
        }
        return null;
    }

    public int insert(BangGia bangGia) throws Exception {
        // Khi thêm mới: mặc định ngày = current date (nếu null)
        LocalDate date = bangGia.getNgayApDung() == null ? LocalDate.now() : bangGia.getNgayApDung();

        String sql = "INSERT INTO BangGia(mon_id, gia, ngay_ap_dung) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bangGia.getMonId());
            ps.setBigDecimal(2, bangGia.getGia());
            ps.setDate(3, Date.valueOf(date));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new Exception("Không lấy được banggia_id sau khi insert BangGia.");
    }

    public void update(BangGia bangGia) throws Exception {
        String sql = "UPDATE BangGia SET mon_id = ?, gia = ?, ngay_ap_dung = ? WHERE banggia_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bangGia.getMonId());
            ps.setBigDecimal(2, bangGia.getGia());
            ps.setDate(3, Date.valueOf(bangGia.getNgayApDung()));
            ps.setInt(4, bangGia.getBangGiaId());
            ps.executeUpdate();
        }
    }

    public void delete(int bangGiaId) throws Exception {
        String sql = "DELETE FROM BangGia WHERE banggia_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bangGiaId);
            ps.executeUpdate();
        }
    }

    private BangGia map(ResultSet rs) throws Exception {
        LocalDate ngay = rs.getDate("ngay_ap_dung").toLocalDate();
        return new BangGia(
                rs.getInt("banggia_id"),
                rs.getInt("mon_id"),
                rs.getBigDecimal("gia"),
                ngay
        );
    }
}

