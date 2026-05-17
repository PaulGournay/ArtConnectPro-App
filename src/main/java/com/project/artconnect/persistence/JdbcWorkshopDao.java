package com.project.artconnect.persistence;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWorkshopDao implements WorkshopDao {

    @Override
    public Optional<Workshop> findById(Long id) {
        String sql = "SELECT * FROM view_workshop_details WHERE workshop_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWorkshop(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Workshop> findAll() {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT * FROM view_workshop_details";

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                workshops.add(mapResultSetToWorkshop(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workshops;
    }

    @Override
    public int getParticipantsCount(long workshopId) {
        String sql = "SELECT get_workshop_participants_count(?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, workshopId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void bookWorkshop(long workshopId, long memberId) {
        String sql = "INSERT INTO Booking (booking_date, payment_status, workshop_id, user_id) " +
                "VALUES (NOW(), 'Pending', ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, workshopId);
            stmt.setLong(2, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to book workshop", e);
        }
    }

    @Override
    public void save(Workshop workshop) {
        String call = "{CALL create_workshop_with_artist(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = ConnectionManager.getConnection();
                CallableStatement cs = conn.prepareCall(call)) {
            cs.setString(1, workshop.getInstructor().getName());
            cs.setString(2, workshop.getInstructor().getContactEmail());
            cs.setString(3, workshop.getTitle());
            cs.setDate(4, workshop.getDate() != null ? Date.valueOf(workshop.getDate().toLocalDate()) : null);
            cs.setInt(5, workshop.getMaxParticipants());
            cs.setBigDecimal(6, java.math.BigDecimal.valueOf(workshop.getPrice()));
            cs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String update = "UPDATE Workshop SET duration_minutes = ?, location = ?, description = ?, level = ? WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setInt(1, workshop.getDurationMinutes());
            stmt.setString(2, workshop.getLocation());
            stmt.setString(3, workshop.getDescription());
            stmt.setString(4, workshop.getLevel());
            stmt.setString(5, workshop.getTitle());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Workshop workshop) {
        String sql = "UPDATE Workshop SET date = ?, duration_minutes = ?, max_participants = ?, price = ?, artist_id = ?, location = ?, description = ?, level = ? WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, workshop.getDate() != null ? Date.valueOf(workshop.getDate().toLocalDate()) : null);
            stmt.setInt(2, workshop.getDurationMinutes());
            stmt.setInt(3, workshop.getMaxParticipants());
            stmt.setDouble(4, workshop.getPrice());
            stmt.setInt(5, getArtistIdByName(conn, workshop.getInstructor().getName()));
            stmt.setString(6, workshop.getLocation());
            stmt.setString(7, workshop.getDescription());
            stmt.setString(8, workshop.getLevel());
            stmt.setString(9, workshop.getTitle());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Workshop WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getArtistIdByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT artist_id FROM Artist WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("artist_id");
                }
            }
        }
        throw new SQLException("Artist not found: " + name);
    }

    private Workshop mapResultSetToWorkshop(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();
        int id = rs.getInt("workshop_id");
        if (!rs.wasNull()) {
            workshop.setId(id);
        }
        workshop.setTitle(rs.getString("title"));

        Date date = rs.getDate("date");
        if (date != null) {
            workshop.setDate(date.toLocalDate().atStartOfDay());
        }

        workshop.setDurationMinutes(rs.getInt("duration_minutes"));
        workshop.setMaxParticipants(rs.getInt("max_participants"));
        workshop.setPrice(rs.getDouble("price"));
        workshop.setLocation(rs.getString("location"));
        workshop.setDescription(rs.getString("description"));
        workshop.setLevel(rs.getString("level"));

        Artist instructor = new Artist();
        instructor.setName(rs.getString("artist_name"));
        workshop.setInstructor(instructor);

        return workshop;
    }
}