package com.project.artconnect.persistence;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcExhibitionDao implements ExhibitionDao {

    @Override
    public List<Exhibition> findAll() {
        List<Exhibition> exhibitions = new ArrayList<>();
        String sql = "SELECT e.*, g.name AS gallery_name " +
                "FROM Exhibition e JOIN Gallery g ON e.gallery_id = g.gallery_id";

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                exhibitions.add(mapResultSetToExhibition(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exhibitions;
    }

    @Override
    public void save(Exhibition exhibition) {
        String sql = "INSERT INTO Exhibition (title, start_date, end_date, description, curator_name, theme, gallery_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            int galleryId = getGalleryIdByName(conn, exhibition.getGallery().getName());
            if (galleryId == -1)
                throw new SQLException("Gallery not found!");

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, exhibition.getTitle());
                stmt.setDate(2, Date.valueOf(exhibition.getStartDate()));
                stmt.setDate(3, exhibition.getEndDate() != null ? Date.valueOf(exhibition.getEndDate()) : null);
                stmt.setString(4, exhibition.getDescription());
                stmt.setString(5, exhibition.getCuratorName());
                stmt.setString(6, exhibition.getTheme());
                stmt.setInt(7, galleryId);

                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        String sql = "UPDATE Exhibition SET start_date = ?, end_date = ?, description = ?, curator_name = ?, theme = ? WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(exhibition.getStartDate()));
            stmt.setDate(2, exhibition.getEndDate() != null ? Date.valueOf(exhibition.getEndDate()) : null);
            stmt.setString(3, exhibition.getDescription());
            stmt.setString(4, exhibition.getCuratorName());
            stmt.setString(5, exhibition.getTheme());
            stmt.setString(6, exhibition.getTitle());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Exhibition WHERE title = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getGalleryIdByName(Connection conn, String galleryName) throws SQLException {
        String sql = "SELECT gallery_id FROM Gallery WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, galleryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("gallery_id");
            }
        }
        return -1;
    }

    private Exhibition mapResultSetToExhibition(ResultSet rs) throws SQLException {
        Exhibition exhibition = new Exhibition();
        exhibition.setTitle(rs.getString("title"));

        Date startDate = rs.getDate("start_date");
        if (startDate != null)
            exhibition.setStartDate(startDate.toLocalDate());

        Date endDate = rs.getDate("end_date");
        if (endDate != null)
            exhibition.setEndDate(endDate.toLocalDate());

        exhibition.setDescription(rs.getString("description"));
        exhibition.setCuratorName(rs.getString("curator_name"));
        exhibition.setTheme(rs.getString("theme"));

        Gallery gallery = new Gallery();
        gallery.setName(rs.getString("gallery_name"));
        exhibition.setGallery(gallery);

        return exhibition;
    }
}