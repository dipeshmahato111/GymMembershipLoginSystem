package database;

import model.Administrator;
import model.Member;
import model.Receptionist;
import model.Role;
import model.Trainer;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the {@code users} table, which backs every
 * {@link User} subclass (single-table inheritance). Every query uses
 * PreparedStatement / parameterized SQL to mitigate SQL injection
 * (SRS 4.5 Security).
 */
public class UserDAO {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<User> findByRole(Role role) throws SQLException {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY full_name";
        List<User> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY role, full_name";
        List<User> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public boolean usernameExists(String username) throws SQLException {
        return exists("SELECT 1 FROM users WHERE username = ?", username);
    }

    public boolean emailExists(String email) throws SQLException {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    private boolean exists(String sql, String param) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserts a new user row. {@code extraField} carries the role-specific
     * attribute: admin level for ADMIN, shift for RECEPTIONIST, or is
     * ignored for MEMBER/TRAINER (Trainer specialization lives in the
     * {@code trainers} table via {@link TrainerDAO}).
     *
     * @return the generated user_id
     */
    public int insertUser(User user, String passwordHash, String extraField) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, email, phone, "
                + "status, join_date, admin_level, shift) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, passwordHash);
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPhone());
            stmt.setString(7, user.getStatus() == null ? "ACTIVE" : user.getStatus());
            stmt.setDate(8, java.sql.Date.valueOf(user.getJoinDate() == null ? LocalDate.now() : user.getJoinDate()));
            stmt.setString(9, user.getRole() == Role.ADMIN ? extraField : null);
            stmt.setString(10, user.getRole() == Role.RECEPTIONIST ? extraField : null);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert user, no generated key returned");
    }

    public void updateProfile(int userId, String fullName, String email, String phone) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
        }
    }

    public void updateStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void updatePasswordHash(int userId, String newHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    public String getPasswordHash(String username) throws SQLException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public int getFailedAttempts(String username) throws SQLException {
        String sql = "SELECT failed_attempts FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public LocalDateTime getLockedUntil(String username) throws SQLException {
        String sql = "SELECT locked_until FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp(1);
                    return ts == null ? null : ts.toLocalDateTime();
                }
                return null;
            }
        }
    }

    public void incrementFailedAttempts(String username) throws SQLException {
        String sql = "UPDATE users SET failed_attempts = failed_attempts + 1 WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public void resetFailedAttempts(String username) throws SQLException {
        String sql = "UPDATE users SET failed_attempts = 0, locked_until = NULL WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public void lockAccount(String username, LocalDateTime until) throws SQLException {
        String sql = "UPDATE users SET locked_until = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(until));
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String status = rs.getString("status");
        java.sql.Date joinDateSql = rs.getDate("join_date");
        LocalDate joinDate = joinDateSql == null ? null : joinDateSql.toLocalDate();
        Role role = Role.valueOf(rs.getString("role"));

        switch (role) {
            case ADMIN:
                return new Administrator(userId, username, fullName, email, phone, status, joinDate,
                        rs.getString("admin_level"));
            case RECEPTIONIST:
                return new Receptionist(userId, username, fullName, email, phone, status, joinDate,
                        rs.getString("shift"));
            case TRAINER:
                return new Trainer(userId, username, fullName, email, phone, status, joinDate, null, null);
            case MEMBER:
            default:
                return new Member(userId, username, fullName, email, phone, status, joinDate);
        }
    }
}
