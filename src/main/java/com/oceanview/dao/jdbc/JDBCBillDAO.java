package com.oceanview.dao.jdbc;

import com.oceanview.config.DataSourceProvider;
import com.oceanview.dao.BillDAO;
import com.oceanview.model.Bill;

import java.sql.*;

public class JDBCBillDAO implements BillDAO {

    private Bill map(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setReservationId(rs.getInt("reservation_id"));
        b.setNights(rs.getInt("nights"));
        b.setRate(rs.getBigDecimal("rate"));
        b.setTotal(rs.getBigDecimal("total"));
        Timestamp printed = rs.getTimestamp("printed_at");
        if (printed != null) b.setPrintedAt(printed.toLocalDateTime());
        return b;
    }

    @Override
    public int save(Bill bill) {
        String sql = "INSERT INTO bills(reservation_id, nights, rate, total) VALUES(?,?,?,?)";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bill.getReservationId());
            ps.setInt(2, bill.getNights());
            ps.setBigDecimal(3, bill.getRate());
            ps.setBigDecimal(4, bill.getTotal());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                throw new SQLException("No generated key returned for bills");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Bill save failed", e);
        }
    }

    @Override
    public Bill findByReservationId(int reservationId) {
        String sql = "SELECT * FROM bills WHERE reservation_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Bill findByReservationId failed", e);
        }
    }
}