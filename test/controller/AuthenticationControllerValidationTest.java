package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the blank-input guard clause of {@link AuthenticationController#login}
 * that runs before any database lookup, so it is runnable without a live
 * MySQL connection. Full login success/failure/lockout behavior additionally
 * requires an integration test against a real database (see README).
 */
class AuthenticationControllerValidationTest {

    private final AuthenticationController controller = new AuthenticationController();

    @Test
    void rejectsBlankUsername() {
        AuthResult result = controller.login("", "somePassword");
        assertFalse(result.isSuccess());
    }

    @Test
    void rejectsBlankPassword() {
        AuthResult result = controller.login("someuser", "");
        assertFalse(result.isSuccess());
    }

    @Test
    void rejectsNullCredentials() {
        AuthResult result = controller.login(null, null);
        assertFalse(result.isSuccess());
    }
}
