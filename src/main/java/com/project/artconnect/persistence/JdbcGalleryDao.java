package com.project.artconnect.persistence;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGalleryDao implements GalleryDao {

    @Override
    public Optional<Gallery> findById(Long id) {
        String sql = "SELECT * FROM Gallery WHERE gallery_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGallery(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Gallery> findAll() {
        List<Gallery> galleries = new ArrayList<>();
        String sql = "SELECT * FROM Gallery";

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                galleries.add(mapResultSetToGallery(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return galleries;
    }

    @Override
    public void save(Gallery gallery) {
        String sql = "INSERT INTO Gallery (name, address, owner_name, opening_hours, contact_phone, rating, website) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gallery.getName());
            stmt.setString(2, gallery.getAddress());
            stmt.setString(3, gallery.getOwnerName());
            stmt.setString(4, gallery.getOpeningHours());
            stmt.setString(5, gallery.getContactPhone());
            stmt.setString(6, gallery.getRating() + " stars");
            stmt.setString(7, gallery.getWebsite());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Gallery gallery) {
        String sql = "UPDATE Gallery SET address = ?, owner_name = ?, opening_hours = ?, contact_phone = ?, rating = ?, website = ? WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gallery.getAddress());
            stmt.setString(2, gallery.getOwnerName());
            stmt.setString(3, gallery.getOpeningHours());
            stmt.setString(4, gallery.getContactPhone());
            stmt.setString(5, gallery.getRating() + " stars");
            stmt.setString(6, gallery.getWebsite());
            stmt.setString(7, gallery.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        String sql = "DELETE FROM Gallery WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Gallery mapResultSetToGallery(ResultSet rs) throws SQLException {
        Gallery gallery = new Gallery();
        gallery.setName(rs.getString("name"));
        gallery.setAddress(rs.getString("address"));
        gallery.setOwnerName(rs.getString("owner_name"));
        gallery.setOpeningHours(rs.getString("opening_hours"));
        gallery.setContactPhone(rs.getString("contact_phone"));

        String ratingStr = rs.getString("rating");
        if (ratingStr != null && ratingStr.contains(" stars")) {
            try {
                gallery.setRating(Double.parseDouble(ratingStr.replace(" stars", "")));
            } catch (NumberFormatException ignored) {
            }
        }

        gallery.setWebsite(rs.getString("website"));
        return gallery;
    }
}