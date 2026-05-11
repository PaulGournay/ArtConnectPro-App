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
        String sql = "SELECT w.*, a.name AS artist_name " +
                "FROM Workshop w JOIN Artist a ON w.artist_id = a.artist_id " +
                "WHERE w.workshop_id = ?";
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
        String sql = "SELECT w.*, a.name AS artist_name " +
                "FROM Workshop w JOIN Artist a ON w.artist_id = a.artist_id";

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

    private Workshop mapResultSetToWorkshop(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();
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