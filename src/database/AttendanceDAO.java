package database;

import model.Attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Data access object for the {@code attendance} table. */
public class AttendanceDAO {

    public int checkIn(int memberId, LocalDateTime time) throws SQLException {
        String sql = "INSERT INTO attendance (member_id, check_in_time) VALUES (?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, memberId);
            stmt.setTimestamp(2, Timestamp.valueOf(time));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to record check-in");
    }

    public void checkOut(int attendanceId, LocalDateTime time) throws SQLException {
        String sql = "UPDATE attendance SET check_out_time = ? WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(time));
            stmt.setInt(2, attendanceId);
            stmt.executeUpdate();
        }
    }

    /** Returns the member's currently open (not checked-out) attendance record, if any. */
    public Attendance findOpenByMember(int memberId) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE member_id = ? AND check_out_time IS NULL "
                + "ORDER BY check_in_time DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Attendance> findByMember(int memberId) throws SQLException {
        String sql = "SELECT * FROM attendance WHERE member_id = ? ORDER BY check_in_time DESC";
        List<Attendance> list = new ArrayList<>();
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

    public List<Attendance> findToday() throws SQLException {
        String sql = "SELECT * FROM attendance WHERE DATE(check_in_time) = CURDATE() ORDER BY check_in_time DESC";
        List<Attendance> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int countToday() throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance WHERE DATE(check_in_time) = CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Timestamp checkIn = rs.getTimestamp("check_in_time");
        Timestamp checkOut = rs.getTimestamp("check_out_time");
        return new Attendance(
                rs.getInt("attendance_id"),
                rs.getInt("member_id"),
                checkIn == null ? null : checkIn.toLocalDateTime(),
                checkOut == null ? null : checkOut.toLocalDateTime()
        );
    }
}
