package database;

import model.Administrator;
import model.MembershipTier;
import model.Role;
import security.PasswordUtil;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Bootstraps a freshly created database (see {@code db/schema.sql}) with a
 * default Administrator account and starter membership tiers, so the
 * application is usable immediately after the schema is loaded.
 */
public final class DbInitializer {

    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";

    private DbInitializer() {
    }

    /** Idempotent: safe to call every time the application starts. */
    public static void initialize() {
        try {
            seedDefaultAdmin();
            seedDefaultTiers();
        } catch (SQLException e) {
            System.err.println("Warning: could not initialize default data - " + e.getMessage());
        }
    }

    private static void seedDefaultAdmin() throws SQLException {
        UserDAO userDAO = new UserDAO();
        if (userDAO.findByUsername(DEFAULT_ADMIN_USERNAME) != null) {
            return;
        }
        Administrator admin = new Administrator(0, DEFAULT_ADMIN_USERNAME, "System Administrator",
                "admin@gym.local", "000-000-0000", "ACTIVE", LocalDate.now(), "SUPER");
        admin.setRole(Role.ADMIN);
        String hash = PasswordUtil.hash(DEFAULT_ADMIN_PASSWORD);
        userDAO.insertUser(admin, hash, "SUPER");
    }

    private static void seedDefaultTiers() throws SQLException {
        MembershipTierDAO tierDAO = new MembershipTierDAO();
        if (tierDAO.count() > 0) {
            return;
        }
        tierDAO.insert(new MembershipTier(0, "Monthly", 39.99, 1));
        tierDAO.insert(new MembershipTier(0, "Annual", 399.99, 12));
        tierDAO.insert(new MembershipTier(0, "VIP", 799.99, 12));
    }
}
