package com.oceanview.dao.jdbc;

import com.oceanview.config.DataSourceProvider;
import com.oceanview.dao.AdminDAO;
import com.oceanview.model.Admin;

import java.sql.*;

public class JDBCAdminDAO implements AdminDAO {

    private Admin map(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setAdminId(rs.getInt("admin_id"));
        a.setUsername(rs.getString("username"));
        a.setPasswordHash(rs.getString("password_hash"));
        a.setRole(rs.getString("role"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) a.setCreatedAt(created.toLocalDateTime());
        return a;
    }

    @Override
    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM admins WHERE username=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Admin findByUsername failed", e);
        }
    }

    @Override
    public Admin findById(int adminId) {
        String sql = "SELECT * FROM admins WHERE admin_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, adminId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Admin findById failed", e);
        }
    }

    @Override
    public void update(Admin admin) {
        String sql = "UPDATE admins SET username=?, role=? WHERE admin_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getRole());
            ps.setInt(3, admin.getAdminId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Admin update failed", e);
        }
    }

    @Override
    public void updatePassword(int adminId, String passwordHash) {
        String sql = "UPDATE admins SET password_hash=? WHERE admin_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, adminId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Admin updatePassword failed", e);
        }
    }
}