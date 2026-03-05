package com.oceanview.dao.jdbc;

import com.oceanview.config.DataSourceProvider;
import com.oceanview.dao.RoomTypeDAO;
import com.oceanview.model.RoomType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCRoomTypeDAO implements RoomTypeDAO {

    private RoomType map(ResultSet rs) throws SQLException {
        RoomType rt = new RoomType();
        rt.setRoomTypeId(rs.getInt("room_type_id"));
        rt.setTypeName(rs.getString("type_name"));
        rt.setNightlyRate(rs.getBigDecimal("nightly_rate"));
        rt.setActive(rs.getInt("active") == 1);

        rt.setDescription(rs.getString("description"));
        rt.setAdultsCount(rs.getInt("adults_count"));
        rt.setChildrenCount(rs.getInt("children_count"));
        rt.setRoomsCount(rs.getInt("rooms_count"));
        rt.setPhotoPath(rs.getString("photo_path"));
        return rt;
    }

    @Override
    public List<RoomType> findAllActive() {
        String sql = "SELECT * FROM room_types WHERE active=1 ORDER BY room_type_id";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<RoomType> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("findAllActive failed", e);
        }
    }

    @Override
    public List<RoomType> findAll() {
        String sql = "SELECT * FROM room_types ORDER BY room_type_id";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // DEBUG: print JDBC url once
            System.out.println("JDBC URL = " + con.getMetaData().getURL());

            List<RoomType> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
    }

    @Override
    public RoomType findById(int id) {
        String sql = "SELECT * FROM room_types WHERE room_type_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("findById failed", e);
        }
    }

    @Override
    public int create(RoomType rt) {
        String sql = """
                    INSERT INTO room_types(
                      type_name, nightly_rate, active,
                      description, adults_count, children_count, rooms_count, photo_path
                    ) VALUES(?,?,?,?,?,?,?,?)
                """;

        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, rt.getTypeName());
            ps.setBigDecimal(2, rt.getNightlyRate());
            ps.setInt(3, rt.isActive() ? 1 : 0);

            ps.setString(4, rt.getDescription());
            ps.setInt(5, rt.getAdultsCount());
            ps.setInt(6, rt.getChildrenCount());
            ps.setInt(7, rt.getRoomsCount());
            ps.setString(8, rt.getPhotoPath());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("create room_type failed", e);
        }
    }

    @Override
    public void update(RoomType rt) {
        String sql = """
                    UPDATE room_types SET
                      type_name=?, nightly_rate=?, active=?,
                      description=?, adults_count=?, children_count=?, rooms_count=?, photo_path=?
                    WHERE room_type_id=?
                """;

        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, rt.getTypeName());
            ps.setBigDecimal(2, rt.getNightlyRate());
            ps.setInt(3, rt.isActive() ? 1 : 0);

            ps.setString(4, rt.getDescription());
            ps.setInt(5, rt.getAdultsCount());
            ps.setInt(6, rt.getChildrenCount());
            ps.setInt(7, rt.getRoomsCount());
            ps.setString(8, rt.getPhotoPath());

            ps.setInt(9, rt.getRoomTypeId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("update room_type failed", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM room_types WHERE room_type_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("delete failed", e);
        }
    }

    @Override
    public void setActive(int id, boolean active) {
        String sql = "UPDATE room_types SET active=? WHERE room_type_id=?";
        try (Connection con = DataSourceProvider.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("setActive failed", e);
        }
    }
}