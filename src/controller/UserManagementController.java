package controller;

import database.TrainerDAO;
import database.UserDAO;
import model.Administrator;
import model.Receptionist;
import model.Role;
import model.Trainer;
import model.User;
import security.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Admin-only user management (Manage Users use case): create staff
 * accounts (Trainer/Receptionist/Admin), suspend/reactivate, and delete.
 * Enforces RBAC by only being invoked from the Admin Dashboard (SRS 4.5).
 */
public class UserManagementController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private final UserDAO userDAO = new UserDAO();
    private final TrainerDAO trainerDAO = new TrainerDAO();

    public List<User> listAll() {
        try {
            return userDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    /**
     * Creates a staff or admin account. {@code extra} holds the
     * role-specific attribute: specialization (Trainer), shift
     * (Receptionist), or admin level (Administrator).
     */
    public Result createStaff(String username, String password, String fullName, String email, String phone,
                               Role role, String extra) {
        if (isBlank(username) || isBlank(password) || isBlank(fullName) || isBlank(email)) {
            return Result.fail("Please fill in all required fields.");
        }
        if (password.length() < 6) {
            return Result.fail("Password must be at least 6 characters.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return Result.fail("Invalid email format. Please include an '@' symbol.");
        }
        if (role == Role.MEMBER) {
            return Result.fail("Use member registration for member accounts.");
        }
        try {
            if (userDAO.usernameExists(username)) {
                return Result.fail("That username is already taken.");
            }
            if (userDAO.emailExists(email)) {
                return Result.fail("Duplicate email address.");
            }
            User user = buildUser(role, username, fullName, email, phone, extra);
            String hash = PasswordUtil.hash(password);
            int userId = userDAO.insertUser(user, hash, extra);

            if (role == Role.TRAINER) {
                trainerDAO.insert(userId, extra);
            }
            return Result.ok(role + " account created. User ID: " + userId, userId);
        } catch (SQLException e) {
            return Result.fail("Could not create account: " + e.getMessage());
        }
    }

    private User buildUser(Role role, String username, String fullName, String email, String phone, String extra) {
        switch (role) {
            case ADMIN:
                return new Administrator(0, username.trim(), fullName.trim(), email.trim(), phone, "ACTIVE",
                        LocalDate.now(), extra);
            case RECEPTIONIST:
                return new Receptionist(0, username.trim(), fullName.trim(), email.trim(), phone, "ACTIVE",
                        LocalDate.now(), extra);
            case TRAINER:
            default:
                Trainer t = new Trainer(0, username.trim(), fullName.trim(), email.trim(), phone, "ACTIVE",
                        LocalDate.now(), null, extra);
                t.setRole(Role.TRAINER);
                return t;
        }
    }

    public Result suspend(int userId) {
        return setStatus(userId, "SUSPENDED", "Account suspended.");
    }

    public Result activate(int userId) {
        return setStatus(userId, "ACTIVE", "Account reactivated.");
    }

    private Result setStatus(int userId, String status, String message) {
        try {
            userDAO.updateStatus(userId, status);
            return Result.ok(message);
        } catch (SQLException e) {
            return Result.fail("Could not update account: " + e.getMessage());
        }
    }

    public Result deleteUser(int userId) {
        try {
            userDAO.deleteUser(userId);
            return Result.ok("Account deleted.");
        } catch (SQLException e) {
            return Result.fail("Could not delete account: " + e.getMessage());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
