package dao;

import model.MonAn;
import model.MonAnWithPriceDTO;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng MonAn.
 * <p>
 * Lưu ý: giá (gia) nằm ở bảng BangGia, không nằm trong MonAn.
 */
public class MonAnDAO {

    public List<MonAnWithPriceDTO> findAllWithLatestPrice() {
        String sql =
                "SELECT m.mon_id, m.ten_mon, m.loai_id, l.ten_loai, m.trang_thai, bg.gia " +
                        "FROM MonAn m " +
                        "JOIN LoaiMonAn l ON m.loai_id = l.loai_id " +
                        "OUTER APPLY ( " +
                        "   SELECT TOP 1 gia " +
                        "   FROM BangGia " +
                        "   WHERE mon_id = m.mon_id " +
                        "   ORDER BY ngay_ap_dung DESC, banggia_id DESC " +
                        ") bg " +
                        "ORDER BY m.mon_id DESC";

        List<MonAnWithPriceDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapDto(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MonAnWithPriceDTO> findByLoaiIdWithLatestPrice(int loaiId) {
        String sql =
                "SELECT m.mon_id, m.ten_mon, m.loai_id, l.ten_loai, m.trang_thai, bg.gia " +
                        "FROM MonAn m " +
                        "JOIN LoaiMonAn l ON m.loai_id = l.loai_id " +
                        "OUTER APPLY ( " +
                        "   SELECT TOP 1 gia " +
                        "   FROM BangGia " +
                        "   WHERE mon_id = m.mon_id " +
                        "   ORDER BY ngay_ap_dung DESC, banggia_id DESC " +
                        ") bg " +
                        "WHERE m.loai_id = ? " +
                        "ORDER BY m.mon_id DESC";

        List<MonAnWithPriceDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, loaiId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDto(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Search món theo:
     * - keyword: tìm theo tên (LIKE)
     * - loaiId: lọc theo loại (nullable)
     * - monId: lọc theo id (nullable)
     */
    public List<MonAnWithPriceDTO> searchWithLatestPrice(String keyword, Integer loaiId, Integer monId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.mon_id, m.ten_mon, m.loai_id, l.ten_loai, m.trang_thai, bg.gia ")
                .append("FROM MonAn m ")
                .append("JOIN LoaiMonAn l ON m.loai_id = l.loai_id ")
                .append("OUTER APPLY ( ")
                .append("   SELECT TOP 1 gia ")
                .append("   FROM BangGia ")
                .append("   WHERE mon_id = m.mon_id ")
                .append("   ORDER BY ngay_ap_dung DESC, banggia_id DESC ")
                .append(") bg ")
                .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND m.ten_mon LIKE ? ");
            params.add("%" + keyword.trim() + "%");
        }
        if (loaiId != null) {
            sql.append("AND m.loai_id = ? ");
            params.add(loaiId);
        }
        if (monId != null) {
            sql.append("AND m.mon_id = ? ");
            params.add(monId);
        }
        sql.append("ORDER BY m.mon_id DESC");

        List<MonAnWithPriceDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDto(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public MonAn findById(int monId) {
        String sql = "SELECT mon_id, ten_mon, loai_id, trang_thai FROM MonAn WHERE mon_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, monId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MonAn(
                            rs.getInt("mon_id"),
                            rs.getString("ten_mon"),
                            rs.getInt("loai_id"),
                            rs.getString("trang_thai")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(MonAn monAn) throws Exception {
        String sql = "INSERT INTO MonAn(ten_mon, loai_id, trang_thai) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, monAn.getTenMon());
            ps.setInt(2, monAn.getLoaiId());
            ps.setString(3, monAn.getTrangThai() == null ? "CON" : monAn.getTrangThai());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new Exception("Không lấy được mon_id sau khi insert MonAn.");
    }

    public void update(MonAn monAn) throws Exception {
        String sql = "UPDATE MonAn SET ten_mon = ?, loai_id = ?, trang_thai = ? WHERE mon_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, monAn.getTenMon());
            ps.setInt(2, monAn.getLoaiId());
            ps.setString(3, monAn.getTrangThai());
            ps.setInt(4, monAn.getMonId());
            ps.executeUpdate();
        }
    }

    public void delete(int monId) throws Exception {
        // Lưu ý: cần xóa bảng giá trước nếu có FK.
        String sql = "DELETE FROM MonAn WHERE mon_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, monId);
            ps.executeUpdate();
        }
    }

    private MonAnWithPriceDTO mapDto(ResultSet rs) throws Exception {
        BigDecimal gia = rs.getBigDecimal("gia");
        return new MonAnWithPriceDTO(
                rs.getInt("mon_id"),
                rs.getString("ten_mon"),
                rs.getInt("loai_id"),
                rs.getString("ten_loai"),
                rs.getString("trang_thai"),
                gia == null ? BigDecimal.ZERO : gia
        );
    }
}

