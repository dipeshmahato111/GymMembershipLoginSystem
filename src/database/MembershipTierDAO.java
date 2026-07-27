package database;

import model.MembershipTier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for {@code membership_tiers}, letting an Administrator
 * change subscription pricing from the Admin Dashboard without a rebuild
 * (SRS 4.4 Maintainability).
 */
public class MembershipTierDAO {

    public List<MembershipTier> findAll() throws SQLException {
        String sql = "SELECT * FROM membership_tiers ORDER BY price";
        List<MembershipTier> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public MembershipTier findByName(String tierName) throws SQLException {
        String sql = "SELECT * FROM membership_tiers WHERE tier_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tierName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public int insert(MembershipTier t) throws SQLException {
        String sql = "INSERT INTO membership_tiers (tier_name, price, duration_months) VALUES (?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, t.getTierName());
            stmt.setDouble(2, t.getPrice());
            stmt.setInt(3, t.getDurationMonths());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert membership tier");
    }

    public void update(MembershipTier t) throws SQLException {
        String sql = "UPDATE membership_tiers SET tier_name = ?, price = ?, duration_months = ? WHERE tier_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getTierName());
            stmt.setDouble(2, t.getPrice());
            stmt.setInt(3, t.getDurationMonths());
            stmt.setInt(4, t.getTierId());
            stmt.executeUpdate();
        }
    }

    public void delete(int tierId) throws SQLException {
        String sql = "DELETE FROM membership_tiers WHERE tier_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tierId);
            stmt.executeUpdate();
        }
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM membership_tiers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private MembershipTier mapRow(ResultSet rs) throws SQLException {
        return new MembershipTier(
                rs.getInt("tier_id"),
                rs.getString("tier_name"),
                rs.getDouble("price"),
                rs.getInt("duration_months")
        );
    }
}
