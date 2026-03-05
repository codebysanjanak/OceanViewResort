package com.oceanview.dao.jdbc;

import com.oceanview.config.DataSourceProvider;
import com.oceanview.dao.GuestDAO;
import com.oceanview.model.Guest;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class JDBCGuestDAO implements GuestDAO {

    private Guest map(ResultSet rs) throws SQLException {
        Guest g = new Guest();
        g.setGuestId(rs.getInt("guest_id"));
        g.setName(rs.getString("name"));
        g.setEmail(rs.getString("email"));
        g.setPhone(rs.getString("phone"));
        g.setAddress(rs.getString("address"));
        g.setPasswordHash(rs.getString("password_hash"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) g.setCreatedAt(created.toLocalDateTime());
        return g;
    }

    @Override
    public int create(Guest guest) {
        String sql = "INSERT INTO guests(name,email,phone,address,password_hash) VALUES (?,?,?,?,?)";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, guest.getName());
            ps.setString(2, guest.getEmail());
            ps.setString(3, guest.getPhone());
            ps.setString(4, guest.getAddress());
            ps.setString(5, guest.getPasswordHash());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
                throw new SQLException("No generated key returned for guests");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Guest create failed", e);
        }
    }

    @Override
    public Guest findByEmail(String email) {
        String sql = "SELECT * FROM guests WHERE email=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Guest findByEmail failed", e);
        }
    }

    @Override
    public Guest findById(int guestId) {
        String sql = "SELECT * FROM guests WHERE guest_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, guestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Guest findById failed", e);
        }
    }

    @Override
    public void update(Guest guest) {
        String sql = "UPDATE guests SET name=?, email=?, phone=?, address=? WHERE guest_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, guest.getName());
            ps.setString(2, guest.getEmail());
            ps.setString(3, guest.getPhone());
            ps.setString(4, guest.getAddress());
            ps.setInt(5, guest.getGuestId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Guest update failed", e);
        }
    }

    @Override
    public void updatePassword(int guestId, String passwordHash) {
        String sql = "UPDATE guests SET password_hash=? WHERE guest_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, guestId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Guest updatePassword failed", e);
        }
    }

    @Override
    public List<Guest> findAll() {

        String sql = "SELECT * FROM guests ORDER BY created_at DESC";

        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Guest> guests = new ArrayList<>();

            while (rs.next()) {
                guests.add(map(rs));
            }

            return guests;

        } catch (SQLException e) {
            throw new RuntimeException("Guest findAll failed", e);
        }
    }
}