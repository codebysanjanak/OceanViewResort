package com.oceanview.dao.jdbc;

import com.oceanview.config.DataSourceProvider;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCReservationDAO implements ReservationDAO {

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationNo(rs.getString("reservation_no"));
        r.setGuestId(rs.getInt("guest_id"));
        r.setRoomTypeId(rs.getInt("room_type_id"));
        Date in = rs.getDate("check_in");
        Date out = rs.getDate("check_out");
        if (in != null) r.setCheckIn(in.toLocalDate());
        if (out != null) r.setCheckOut(out.toLocalDate());
        r.setStatus(rs.getString("status"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) r.setCreatedAt(created.toLocalDateTime());
        return r;
    }

    @Override
    public int create(Reservation r) {
        String sql = """
            INSERT INTO reservations(reservation_no, guest_id, room_type_id, check_in, check_out, status)
            VALUES (?,?,?,?,?,?)
        """;
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getReservationNo());
            ps.setInt(2, r.getGuestId());
            ps.setInt(3, r.getRoomTypeId());
            ps.setDate(4, Date.valueOf(r.getCheckIn()));
            ps.setDate(5, Date.valueOf(r.getCheckOut()));
            ps.setString(6, r.getStatus() == null ? "BOOKED" : r.getStatus());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                throw new SQLException("No generated key returned for reservations");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation create failed", e);
        }
    }

    @Override
    public Reservation findByReservationNo(String reservationNo) {
        String sql = "SELECT * FROM reservations WHERE reservation_no=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reservationNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation findByReservationNo failed", e);
        }
    }

    @Override
    public List<Reservation> findByGuestId(int guestId) {
        String sql = "SELECT * FROM reservations WHERE guest_id=? ORDER BY created_at DESC";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, guestId);

            try (ResultSet rs = ps.executeQuery()) {
                List<Reservation> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation findByGuestId failed", e);
        }
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservations ORDER BY created_at DESC";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Reservation> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Reservation findAll failed", e);
        }
    }

    @Override
    public void cancelByGuest(int guestId, String reservationNo) {
        String sql = "UPDATE reservations SET status='CANCELLED' WHERE guest_id=? AND reservation_no=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, guestId);
            ps.setString(2, reservationNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Reservation cancelByGuest failed", e);
        }
    }

    @Override
    public int countOverlaps(int roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // overlap rule: newCheckIn < existingCheckOut AND newCheckOut > existingCheckIn
        String sql = """
            SELECT COUNT(*) AS c
            FROM reservations
            WHERE room_type_id=?
              AND status='BOOKED'
              AND (? < check_out AND ? > check_in)
        """;
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomTypeId);
            ps.setDate(2, Date.valueOf(checkIn));
            ps.setDate(3, Date.valueOf(checkOut));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("c");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Reservation countOverlaps failed", e);
        }
    }
}