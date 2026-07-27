package database;

import model.Membership;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Data access object for the {@code memberships} table. */
public class MembershipDAO {

    public int insert(Membership m) throws SQLException {
        String sql = "INSERT INTO memberships (member_id, tier_name, price, start_date, end_date, status) "
                + "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, m.getMemberId());
            stmt.setString(2, m.getTierName());
            stmt.setDouble(3, m.getPrice());
            stmt.setDate(4, java.sql.Date.valueOf(m.getStartDate()));
            stmt.setDate(5, java.sql.Date.valueOf(m.getEndDate()));
            stmt.setString(6, m.getStatus());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert membership, no generated key returned");
    }

    public Membership findById(int membershipId) throws SQLException {
        String sql = "SELECT * FROM memberships WHERE membership_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, membershipId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** Most recent membership row for a member, active or not. */
    public Membership findLatestByMemberId(int memberId) throws SQLException {
        String sql = "SELECT * FROM memberships WHERE member_id = ? ORDER BY end_date DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Membership> findByMemberId(int memberId) throws SQLException {
        String sql = "SELECT * FROM memberships WHERE member_id = ? ORDER BY start_date DESC";
        List<Membership> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Membership> findAll() throws SQLException {
        String sql = "SELECT * FROM memberships ORDER BY membership_id DESC";
        List<Membership> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Membership> findExpiringWithin(int days) throws SQLException {
        String sql = "SELECT * FROM memberships WHERE status = 'ACTIVE' AND end_date BETWEEN CURDATE() "
                + "AND DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY end_date";
        List<Membership> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM memberships WHERE status = 'ACTIVE' AND end_date >= CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public void update(Membership m) throws SQLException {
        String sql = "UPDATE memberships SET tier_name = ?, price = ?, start_date = ?, end_date = ?, "
                + "status = ? WHERE membership_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getTierName());
            stmt.setDouble(2, m.getPrice());
            stmt.setDate(3, java.sql.Date.valueOf(m.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(m.getEndDate()));
            stmt.setString(5, m.getStatus());
            stmt.setInt(6, m.getMembershipId());
            stmt.executeUpdate();
        }
    }

    private Membership mapRow(ResultSet rs) throws SQLException {
        return new Membership(
                rs.getInt("membership_id"),
                rs.getInt("member_id"),
                rs.getString("tier_name"),
                rs.getDouble("price"),
                toLocalDate(rs.getDate("start_date")),
                toLocalDate(rs.getDate("end_date")),
                rs.getString("status")
        );
    }

    private LocalDate toLocalDate(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
