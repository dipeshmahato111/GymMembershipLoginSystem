package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the input-validation branches of {@link MembershipController#registerMember}
 * that fail fast before any database access is attempted, so they run
 * without a live MySQL connection. Matches the "Missing required
 * information" alternate flow from SRS 3.2.1.
 */
class MembershipControllerValidationTest {

    private final MembershipController controller = new MembershipController();

    @Test
    void rejectsBlankRequiredFields() {
        Result r = controller.registerMember("", "password1", "Jane Doe", "jane@example.com", "555-1234", "Monthly");
        assertFalse(r.isSuccess());
    }

    @Test
    void rejectsShortPassword() {
        Result r = controller.registerMember("janedoe", "abc", "Jane Doe", "jane@example.com", "555-1234", "Monthly");
        assertFalse(r.isSuccess());
    }

    @Test
    void rejectsInvalidEmailFormat() {
        Result r = controller.registerMember("janedoe", "password1", "Jane Doe", "not-an-email", "555-1234", "Monthly");
        assertFalse(r.isSuccess());
    }
}
