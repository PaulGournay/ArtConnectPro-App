package com.project.artconnect.persistence;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommunityMemberDao implements CommunityMemberDao {

    @Override
    public Optional<CommunityMember> findById(Long id) {
        String sql = "SELECT * FROM CommunityMember WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<CommunityMember> findAll() {
        List<CommunityMember> members = new ArrayList<>();
        String sql = "SELECT * FROM CommunityMember";

        try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public void save(CommunityMember member) {
        String sql = "INSERT INTO CommunityMember (name, email, phone, city, membership_type, birth_year) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getCity());
            stmt.setString(5, member.getMembershipType());
            if (member.getBirthYear() != null) {
                stmt.setInt(6, member.getBirthYear());
            } else {
                stmt.setNull(6, java.sql.Types.SMALLINT);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(CommunityMember member) {
        String sql = "UPDATE CommunityMember SET email = ?, phone = ?, city = ?, membership_type = ?, birth_year = ? WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getEmail());
            stmt.setString(2, member.getPhone());
            stmt.setString(3, member.getCity());
            stmt.setString(4, member.getMembershipType());
            if (member.getBirthYear() != null) {
                stmt.setInt(5, member.getBirthYear());
            } else {
                stmt.setNull(5, java.sql.Types.SMALLINT);
            }
            stmt.setString(6, member.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        String sql = "DELETE FROM CommunityMember WHERE name = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private CommunityMember mapResultSetToMember(ResultSet rs) throws SQLException {
        CommunityMember member = new CommunityMember();
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setCity(rs.getString("city"));
        member.setMembershipType(rs.getString("membership_type"));

        int birthYear = rs.getInt("birth_year");
        if (!rs.wasNull()) {
            member.setBirthYear(birthYear);
        }

        return member;
    }
}