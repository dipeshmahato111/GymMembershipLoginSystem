package database;

import model.Booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Data access object for the {@code bookings} table. */
public class BookingDAO {

    public int insert(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings (member_id, class_id, booking_date, status) VALUES (?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, b.getMemberId());
            stmt.setInt(2, b.getClassId());
            stmt.setTimestamp(3, Timestamp.valueOf(b.getBookingDate()));
            stmt.setString(4, b.getStatus());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert booking");
    }

    public boolean existsActiveBooking(int memberId, int classId) throws SQLException {
        String sql = "SELECT 1 FROM bookings WHERE member_id = ? AND class_id = ? AND status = 'CONFIRMED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setInt(2, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Booking> findByMember(int memberId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE member_id = ? ORDER BY booking_date DESC";
        List<Booking> list = new ArrayList<>();
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

    public List<Booking> findByClass(int classId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE class_id = ? AND status = 'CONFIRMED' ORDER BY booking_date";
        List<Booking> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, classId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public void cancel(int bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        }
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("booking_id"),
                rs.getInt("member_id"),
                rs.getInt("class_id"),
                rs.getTimestamp("booking_date").toLocalDateTime(),
                rs.getString("status")
        );
    }
}
