package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation for ArtworkDao.
 */
public class JdbcArtworkDao implements ArtworkDao {

    @Override
    public List<Artwork> findAll() {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT * FROM view_artwork_catalog_with_artist";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                artworks.add(mapResultSetToArtwork(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artworks;
    }

    @Override
    public java.util.Optional<Artwork> findByTitle(String title) {
        String sql = "SELECT * FROM view_artwork_catalog_with_artist WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return java.util.Optional.of(mapResultSetToArtwork(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return java.util.Optional.empty();
    }

    @Override
    public void save(Artwork artwork) {
        String sql = "INSERT INTO Artwork (title, type, medium, dimensions, description, status, creation_year, price, artist_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection()) {
            int artistId = getArtistIdByName(conn, artwork.getArtist().getName());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, artwork.getTitle());
                stmt.setString(2, artwork.getType());
                stmt.setString(3, artwork.getMedium());
                stmt.setString(4, artwork.getDimensions());
                stmt.setString(5, artwork.getDescription());
                stmt.setString(6, artwork.getStatus() != null ? artwork.getStatus().name() : null);
                if (artwork.getCreationYear() != null) {
                    stmt.setInt(7, artwork.getCreationYear());
                } else {
                    stmt.setNull(7, java.sql.Types.SMALLINT);
                }
                stmt.setDouble(8, artwork.getPrice());
                stmt.setInt(9, artistId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Artwork artwork) {
        String sql = "UPDATE Artwork SET type = ?, medium = ?, dimensions = ?, description = ?, status = ?, creation_year = ?, price = ?, artist_id = ? WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection()) {
            int artistId = getArtistIdByName(conn, artwork.getArtist().getName());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, artwork.getType());
                stmt.setString(2, artwork.getMedium());
                stmt.setString(3, artwork.getDimensions());
                stmt.setString(4, artwork.getDescription());
                stmt.setString(5, artwork.getStatus() != null ? artwork.getStatus().name() : null);
                if (artwork.getCreationYear() != null) {
                    stmt.setInt(6, artwork.getCreationYear());
                } else {
                    stmt.setNull(6, java.sql.Types.SMALLINT);
                }
                stmt.setDouble(7, artwork.getPrice());
                stmt.setInt(8, artistId);
                stmt.setString(9, artwork.getTitle());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Artwork WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Artwork> findByArtistName(String artistName) {
        List<Artwork> artworks = new ArrayList<>();
        String sql = "SELECT * FROM view_artwork_catalog_with_artist WHERE artist_name = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, artistName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    artworks.add(mapResultSetToArtwork(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artworks;
    }

    private int getArtistIdByName(Connection conn, String artistName) throws SQLException {
        String sql = "SELECT artist_id FROM Artist WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, artistName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("artist_id");
                } else {
                    throw new SQLException("Artist not found: " + artistName);
                }
            }
        }
    }

    private Artwork mapResultSetToArtwork(ResultSet rs) throws SQLException {
        Artwork artwork = new Artwork();
        artwork.setTitle(rs.getString("title"));
        artwork.setType(rs.getString("type"));
        artwork.setMedium(rs.getString("medium"));
        artwork.setDimensions(rs.getString("dimensions"));
        artwork.setDescription(rs.getString("description"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                artwork.setStatus(Artwork.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        int creationYear = rs.getInt("creation_year");
        if (!rs.wasNull()) {
            artwork.setCreationYear(creationYear);
        }

        artwork.setPrice(rs.getDouble("price"));

        Artist artist = new Artist();
        artist.setName(rs.getString("artist_name"));
        artwork.setArtist(artist);

        return artwork;
    }
}
