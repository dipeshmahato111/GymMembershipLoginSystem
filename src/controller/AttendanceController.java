package controller;

import database.AttendanceDAO;
import database.MembershipDAO;
import database.UserDAO;
import model.Attendance;
import model.Membership;
import model.Role;
import model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Implements the "Member Check In" use case (SRS 3.2.3). */
public class AttendanceController {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Checks a member in by username or numeric member id, after verifying
     * the account is a member in good standing with an active membership.
     */
    public Result checkIn(String usernameOrId) {
        if (usernameOrId == null || usernameOrId.isBlank()) {
            return Result.fail("Enter a username or member ID to check in.");
        }
        try {
            User user = resolveMember(usernameOrId.trim());
            if (user == null) {
                return Result.fail("No matching member found.");
            }
            if (user.getRole() != Role.MEMBER) {
                return Result.fail("Only members can check in.");
            }
            if (!user.isActive()) {
                return Result.fail("This member's account is suspended.");
            }
            Membership membership = membershipDAO.findLatestByMemberId(user.getUserId());
            if (membership == null || !membership.isActive()) {
                return Result.fail("Membership has expired or is suspended. Check-in denied.");
            }
            Attendance open = attendanceDAO.findOpenByMember(user.getUserId());
            if (open != null) {
                return Result.fail(user.getFullName() + " is already checked in (since "
                        + open.getCheckInTime() + ").");
            }
            attendanceDAO.checkIn(user.getUserId(), LocalDateTime.now());
            return Result.ok("Checked in: " + user.getFullName() + " at " + LocalDateTime.now()
                    .toLocalTime().withNano(0));
        } catch (SQLException e) {
            return Result.fail("Check-in failed - database unavailable. " + e.getMessage());
        }
    }

    /**
     * Checks a member out by username or numeric member id. Mirrors
     * {@link #checkIn(String)} so the front desk can use the same
     * identifier for both ends of a visit.
     */
    public Result checkOut(String usernameOrId) {
        if (usernameOrId == null || usernameOrId.isBlank()) {
            return Result.fail("Enter a username or member ID to check out.");
        }
        try {
            User user = resolveMember(usernameOrId.trim());
            if (user == null) {
                return Result.fail("No matching member found.");
            }
            if (user.getRole() != Role.MEMBER) {
                return Result.fail("Only members can check out.");
            }
            return checkOut(user.getUserId());
        } catch (SQLException e) {
            return Result.fail("Check-out failed - database unavailable. " + e.getMessage());
        }
    }

    public Result checkOut(int memberId) {
        try {
            Attendance open = attendanceDAO.findOpenByMember(memberId);
            if (open == null) {
                return Result.fail("No open check-in found for this member.");
            }
            attendanceDAO.checkOut(open.getAttendanceId(), LocalDateTime.now());
            return Result.ok("Checked out successfully.");
        } catch (SQLException e) {
            return Result.fail("Check-out failed: " + e.getMessage());
        }
    }

    public List<Attendance> history(int memberId) {
        try {
            return attendanceDAO.findByMember(memberId);
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Attendance> today() {
        try {
            return attendanceDAO.findToday();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public int todayCount() {
        try {
            return attendanceDAO.countToday();
        } catch (SQLException e) {
            return 0;
        }
    }

    private User resolveMember(String usernameOrId) throws SQLException {
        if (usernameOrId.matches("\\d+")) {
            User user = userDAO.findById(Integer.parseInt(usernameOrId));
            if (user != null) {
                return user;
            }
        }
        return userDAO.findByUsername(usernameOrId);
    }
}
