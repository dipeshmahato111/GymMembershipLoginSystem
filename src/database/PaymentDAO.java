package database;

import model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Data access object for the {@code payments} table. */
public class PaymentDAO {

    public int insert(Payment p) throws SQLException {
        String sql = "INSERT INTO payments (membership_id, amount, payment_date, payment_method, status) "
                + "VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, p.getMembershipId());
            stmt.setDouble(2, p.getAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(p.getPaymentDate()));
            stmt.setString(4, p.getPaymentMethod());
            stmt.setString(5, p.getStatus());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert payment");
    }

    public Payment findById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public List<Payment> findByMembership(int membershipId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE membership_id = ? ORDER BY payment_date DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, membershipId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Payment> findAll() throws SQLException {
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public double sumRevenueBetween(LocalDate start, LocalDate end) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payments WHERE status = 'COMPLETED' "
                + "AND DATE(payment_date) BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(start));
            stmt.setDate(2, java.sql.Date.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getInt("payment_id"),
                rs.getInt("membership_id"),
                rs.getDouble("amount"),
                rs.getTimestamp("payment_date").toLocalDateTime(),
                rs.getString("payment_method"),
                rs.getString("status")
        );
    }
}
