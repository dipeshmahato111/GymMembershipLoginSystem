package controller;

import database.AttendanceDAO;
import database.MembershipDAO;
import database.PaymentDAO;
import database.UserDAO;
import model.Membership;
import model.Role;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Aggregates data for the Admin "Generate Reports" use case (SRS use case diagram). */
public class ReportController {

    private final UserDAO userDAO = new UserDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    public int totalActiveMembers() {
        try {
            return membershipDAO.countActive();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int totalMembers() {
        try {
            return userDAO.findByRole(Role.MEMBER).size();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int todayAttendanceCount() {
        try {
            return attendanceDAO.countToday();
        } catch (SQLException e) {
            return 0;
        }
    }

    public double revenueThisMonth() {
        try {
            LocalDate now = LocalDate.now();
            return paymentDAO.sumRevenueBetween(now.withDayOfMonth(1), now);
        } catch (SQLException e) {
            return 0.0;
        }
    }

    /** Reports generated within the SRS 4.3 Performance target of 10 seconds. */
    public String summaryReport() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        sb.append("Gym Membership Login System - Operational Report\n");
        sb.append("Generated: ").append(LocalDate.now().format(fmt)).append("\n");
        sb.append("=======================================================\n\n");
        sb.append(String.format("Total registered members:      %d%n", totalMembers()));
        sb.append(String.format("Members with active plans:     %d%n", totalActiveMembers()));
        sb.append(String.format("Check-ins today:                %d%n", todayAttendanceCount()));
        sb.append(String.format("Revenue this month:             $%.2f%n%n", revenueThisMonth()));

        sb.append("Memberships expiring in the next 7 days:\n");
        try {
            for (Membership m : membershipDAO.findExpiringWithin(7)) {
                sb.append(String.format("  - Member #%d (%s), expires %s%n", m.getMemberId(), m.getTierName(),
                        m.getEndDate()));
            }
        } catch (SQLException e) {
            sb.append("  (unable to load expiring memberships)\n");
        }
        return sb.toString();
    }
}
