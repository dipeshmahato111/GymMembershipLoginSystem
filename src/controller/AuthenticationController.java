package controller;

import database.UserDAO;
import model.User;
import security.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implements the {@code AuthenticationController} from the SRS class
 * diagram (Figure 6): {@code verifyCredentials}, {@code resetPassword},
 * {@code generateSessionToken}. Also enforces the brute-force lockout
 * policy from SRS 4.2 Robustness (5 failed attempts -> 15 minute lockout).
 */
public class AuthenticationController {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("h:mm a");

    private final UserDAO userDAO = new UserDAO();
    private String currentSessionToken;

    /**
     * Authenticates a username/password pair, applying account lockout and
     * suspension checks. Never reveals whether the failure was due to an
     * unknown username or a wrong password (avoids user enumeration).
     */
    public AuthResult login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new AuthResult(false, "Username and password are required.", null);
        }
        try {
            User user = userDAO.findByUsername(username.trim());
            if (user == null) {
                return new AuthResult(false, "Invalid username or password.", null);
            }

            LocalDateTime lockedUntil = userDAO.getLockedUntil(username);
            if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
                return new AuthResult(false, "Account locked due to repeated failed attempts. "
                        + "Try again after " + lockedUntil.format(TIME_FMT) + ".", null);
            }

            if (!user.isActive()) {
                return new AuthResult(false, "This account has been suspended. Contact an administrator.", null);
            }

            String storedHash = userDAO.getPasswordHash(username);
            if (!PasswordUtil.verify(password, storedHash)) {
                userDAO.incrementFailedAttempts(username);
                int attempts = userDAO.getFailedAttempts(username);
                if (attempts >= MAX_ATTEMPTS) {
                    userDAO.lockAccount(username, LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
                    return new AuthResult(false, "Too many failed attempts. Account locked for "
                            + LOCKOUT_MINUTES + " minutes.", null);
                }
                int remaining = MAX_ATTEMPTS - attempts;
                return new AuthResult(false, "Invalid username or password. " + remaining
                        + " attempt(s) remaining before lockout.", null);
            }

            userDAO.resetFailedAttempts(username);
            currentSessionToken = generateSessionToken();
            return new AuthResult(true, "Login successful.", user);
        } catch (SQLException e) {
            return new AuthResult(false, "Database unavailable. Please try again later.", null);
        }
    }

    /** Verifies credentials without establishing a session (used for re-auth flows). */
    public boolean verifyCredentials(String username, String password) {
        try {
            String storedHash = userDAO.getPasswordHash(username);
            return PasswordUtil.verify(password, storedHash);
        } catch (SQLException e) {
            return false;
        }
    }

    /** Admin-initiated password reset; sets a new password hash for the given user. */
    public Result resetPassword(String username, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            return Result.fail("New password must be at least 6 characters.");
        }
        try {
            User user = userDAO.findByUsername(username);
            if (user == null) {
                return Result.fail("No such user.");
            }
            userDAO.updatePasswordHash(user.getUserId(), PasswordUtil.hash(newPassword));
            userDAO.resetFailedAttempts(username);
            return Result.ok("Password reset successfully.");
        } catch (SQLException e) {
            return Result.fail("Database error: " + e.getMessage());
        }
    }

    /** Produces an opaque per-login session identifier (not persisted; single-user desktop session). */
    public String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    public String getCurrentSessionToken() {
        return currentSessionToken;
    }

    public void logout() {
        currentSessionToken = null;
    }
}
