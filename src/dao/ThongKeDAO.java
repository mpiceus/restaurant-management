package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.HourlyTrendDTO;
import model.ThongKeItemDTO;
import util.DBConnection;

public class ThongKeDAO {

    public int countHoaDon(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS tong_hoa_don " +
                        "FROM HoaDon h " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendDateFilter(sql, params, "h.ngay_tao", from, to);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tong_hoa_don");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public BigDecimal sumDoanhThu(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(h.tong_tien), 0) AS tong_doanh_thu " +
                        "FROM HoaDon h " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendDateFilter(sql, params, "h.ngay_tao", from, to);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("tong_doanh_thu");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public List<ThongKeItemDTO> topMonBanChay(LocalDate from, LocalDate to, int limit) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP (").append(Math.max(limit, 1)).append(") ");
        sql.append("m.mon_id, m.ten_mon, COALESCE(SUM(c.so_luong), 0) AS so_luong ");
        sql.append("FROM ChiTietHoaDon c ");
        sql.append("JOIN HoaDon h ON c.hoadon_id = h.hoadon_id ");
        sql.append("JOIN MonAn m ON c.mon_id = m.mon_id ");
        sql.append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        appendDateFilter(sql, params, "h.ngay_tao", from, to);
        sql.append("GROUP BY m.mon_id, m.ten_mon ");
        sql.append("ORDER BY so_luong DESC, m.ten_mon ASC");
        return queryItems(sql.toString(), params);
    }

    public List<ThongKeItemDTO> topNhanVien(LocalDate from, LocalDate to, int limit) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP (").append(Math.max(limit, 1)).append(") ");
        sql.append("u.user_id, COALESCE(u.fullname, u.username) AS ten, COUNT(*) AS so_luong ");
        sql.append("FROM HoaDon h ");
        sql.append("JOIN Users u ON h.user_id = u.user_id ");
        sql.append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        appendDateFilter(sql, params, "h.ngay_tao", from, to);
        sql.append("GROUP BY u.user_id, COALESCE(u.fullname, u.username) ");
        sql.append("ORDER BY so_luong DESC, ten ASC");
        return queryItems(sql.toString(), params);
    }

    public List<ThongKeItemDTO> topBan(LocalDate from, LocalDate to, int limit) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP (").append(Math.max(limit, 1)).append(") ");
        sql.append("b.ban_id, b.ten_ban, COUNT(*) AS so_luong ");
        sql.append("FROM HoaDon h ");
        sql.append("JOIN Ban b ON h.ban_id = b.ban_id ");
        sql.append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        appendDateFilter(sql, params, "h.ngay_tao", from, to);
        sql.append("GROUP BY b.ban_id, b.ten_ban ");
        sql.append("ORDER BY so_luong DESC, b.ten_ban ASC");
        return queryItems(sql.toString(), params);
    }

    public List<HourlyTrendDTO> hourlyTrend(LocalDate from, LocalDate to) {
        int dayCount = countDistinctDays(from, to);
        Map<Integer, Integer> countsByHour = countInvoicesByHour(from, to);
        List<HourlyTrendDTO> list = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            int count = countsByHour.getOrDefault(hour, 0);
            double average = dayCount == 0 ? 0.0 : (double) count / dayCount;
            list.add(new HourlyTrendDTO(hour, String.format("%02d:00", hour), average));
        }
        return list;
    }

    private int countDistinctDays(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT CAST(h.ngay_tao AS date)) AS tong_ngay " +
                        "FROM HoaDon h " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendDateFilter(sql, params, "h.ngay_tao", from, to);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("tong_ngay");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Map<Integer, Integer> countInvoicesByHour(LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder(
                "SELECT DATEPART(hour, h.ngay_tao) AS gio, COUNT(*) AS so_hd " +
                        "FROM HoaDon h " +
                        "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendDateFilter(sql, params, "h.ngay_tao", from, to);
        sql.append(" GROUP BY DATEPART(hour, h.ngay_tao) ORDER BY gio");

        Map<Integer, Integer> map = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("gio"), rs.getInt("so_hd"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private List<ThongKeItemDTO> queryItems(String sql, List<Object> params) {
        List<ThongKeItemDTO> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ThongKeItemDTO(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getInt(3)
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void appendDateFilter(StringBuilder sql, List<Object> params, String column,
                                  LocalDate from, LocalDate to) {
        if (from != null) {
            sql.append(" AND ").append(column).append(" >= ?");
            params.add(Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND ").append(column).append(" < DATEADD(day, 1, ?)");
            params.add(Date.valueOf(to));
        }
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            if (value instanceof Date) {
                ps.setDate(i + 1, (Date) value);
            } else if (value instanceof Integer) {
                ps.setInt(i + 1, (Integer) value);
            } else if (value instanceof BigDecimal) {
                ps.setBigDecimal(i + 1, (BigDecimal) value);
            } else {
                ps.setObject(i + 1, value);
            }
        }
    }
}
