package database;

import model.FitnessClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Data access object for the {@code fitness_classes} table. */
public class FitnessClassDAO {

    private static final String BASE_SELECT =
            "SELECT fc.*, u.full_name AS trainer_name, "
                    + "(SELECT COUNT(*) FROM bookings b WHERE b.class_id = fc.class_id AND b.status = 'CONFIRMED') AS booked "
                    + "FROM fitness_classes fc "
                    + "JOIN trainers t ON t.trainer_id = fc.trainer_id "
                    + "JOIN users u ON u.user_id = t.user_id ";

    public int insert(FitnessClass fc) throws SQLException {
        String sql = "INSERT INTO fitness_classes (trainer_id, class_name, schedule_time, max_capacity) "
                + "VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, fc.getTrainerId());
            stmt.setString(2, fc.getClassName());
            stmt.setTimestamp(3, Timestamp.valueOf(fc.getScheduleTime()));
            stmt.setInt(4, fc.getMaxCapacity());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert fitness class");
    }

    public FitnessClass findById(int classId) throws SQLException {
        String sql = BASE_SELECT + "WHERE fc.class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<FitnessClass> findAll() throws SQLException {
        String sql = BASE_SELECT + "ORDER BY fc.schedule_time";
        List<FitnessClass> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<FitnessClass> findByTrainer(int trainerId) throws SQLException {
        String sql = BASE_SELECT + "WHERE fc.trainer_id = ? ORDER BY fc.schedule_time";
        List<FitnessClass> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public int countBookings(int classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE class_id = ? AND status = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void update(FitnessClass fc) throws SQLException {
        String sql = "UPDATE fitness_classes SET class_name = ?, schedule_time = ?, max_capacity = ? "
                + "WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fc.getClassName());
            stmt.setTimestamp(2, Timestamp.valueOf(fc.getScheduleTime()));
            stmt.setInt(3, fc.getMaxCapacity());
            stmt.setInt(4, fc.getClassId());
            stmt.executeUpdate();
        }
    }

    public void delete(int classId) throws SQLException {
        String sql = "DELETE FROM fitness_classes WHERE class_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            stmt.executeUpdate();
        }
    }

    private FitnessClass mapRow(ResultSet rs) throws SQLException {
        FitnessClass fc = new FitnessClass(
                rs.getInt("class_id"),
                rs.getInt("trainer_id"),
                rs.getString("class_name"),
                rs.getTimestamp("schedule_time").toLocalDateTime(),
                rs.getInt("max_capacity")
        );
        fc.setTrainerName(rs.getString("trainer_name"));
        fc.setCurrentBookings(rs.getInt("booked"));
        return fc;
    }
}
