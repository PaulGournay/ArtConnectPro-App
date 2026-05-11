package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation for ArtistDao.
 */
public class JdbcArtistDao implements ArtistDao {

    @Override
    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT * FROM Artist";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                artists.add(mapResultSetToArtist(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;
    }

    @Override
    public Optional<Artist> findByName(String name) {
        String sql = "SELECT * FROM Artist WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToArtist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void save(Artist artist) {
        String sql = "INSERT INTO Artist (name, birth_year, contact_email, phone, city, website, social_media, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, artist.getName());
            if (artist.getBirthYear() != null) {
                stmt.setInt(2, artist.getBirthYear());
            } else {
                stmt.setNull(2, java.sql.Types.SMALLINT);
            }
            stmt.setString(3, artist.getContactEmail());
            stmt.setString(4, artist.getPhone());
            stmt.setString(5, artist.getCity());
            stmt.setString(6, artist.getWebsite());
            stmt.setString(7, artist.getSocialMedia());
            stmt.setBoolean(8, artist.isActive());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Artist artist) {
        String sql = "UPDATE Artist SET birth_year = ?, contact_email = ?, phone = ?, city = ?, website = ?, social_media = ?, isActive = ? WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (artist.getBirthYear() != null) {
                stmt.setInt(1, artist.getBirthYear());
            } else {
                stmt.setNull(1, java.sql.Types.SMALLINT);
            }
            stmt.setString(2, artist.getContactEmail());
            stmt.setString(3, artist.getPhone());
            stmt.setString(4, artist.getCity());
            stmt.setString(5, artist.getWebsite());
            stmt.setString(6, artist.getSocialMedia());
            stmt.setBoolean(7, artist.isActive());
            stmt.setString(8, artist.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String artistName) {
        String sql = "DELETE FROM Artist WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, artistName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Artist> findByCity(String city) {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT * FROM Artist WHERE city = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    artists.add(mapResultSetToArtist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;
    }

    private Artist mapResultSetToArtist(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setName(rs.getString("name"));
        int birthYear = rs.getInt("birth_year");
        if (!rs.wasNull()) {
            artist.setBirthYear(birthYear);
        }
        artist.setContactEmail(rs.getString("contact_email"));
        artist.setPhone(rs.getString("phone"));
        artist.setCity(rs.getString("city"));
        artist.setWebsite(rs.getString("website"));
        artist.setSocialMedia(rs.getString("social_media"));
        artist.setActive(rs.getBoolean("isActive"));
        return artist;
    }
}
