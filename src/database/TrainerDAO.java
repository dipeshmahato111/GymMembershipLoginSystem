package database;

import model.Trainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the {@code trainers} table, joined with
 * {@code users} to build fully-populated {@link Trainer} objects.
 */
public class TrainerDAO {

    private static final String BASE_SELECT =
            "SELECT u.*, t.trainer_id, t.specialization FROM trainers t "
                    + "JOIN users u ON u.user_id = t.user_id ";

    public int insert(int userId, String specialization) throws SQLException {
        String sql = "INSERT INTO trainers (user_id, specialization) VALUES (?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.setString(2, specialization);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert trainer");
    }

    public Trainer findByUserId(int userId) throws SQLException {
        String sql = BASE_SELECT + "WHERE u.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public Trainer findByTrainerId(int trainerId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.trainer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Trainer> findAll() throws SQLException {
        String sql = BASE_SELECT + "ORDER BY u.full_name";
        List<Trainer> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Trainer mapRow(ResultSet rs) throws SQLException {
        java.sql.Date joinDateSql = rs.getDate("join_date");
        LocalDate joinDate = joinDateSql == null ? null : joinDateSql.toLocalDate();
        return new Trainer(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("status"),
                joinDate,
                rs.getInt("trainer_id"),
                rs.getString("specialization")
        );
    }
}
