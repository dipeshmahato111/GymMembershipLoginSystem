package controller;

import model.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/** Tests the validation branches of {@link UserManagementController#createStaff}. */
class UserManagementControllerValidationTest {

    private final UserManagementController controller = new UserManagementController();

    @Test
    void rejectsBlankRequiredFields() {
        Result r = controller.createStaff("", "password1", "Trainer Tom", "tom@example.com", "555-0000",
                Role.TRAINER, "Yoga");
        assertFalse(r.isSuccess());
    }

    @Test
    void rejectsShortPassword() {
        Result r = controller.createStaff("tomt", "123", "Trainer Tom", "tom@example.com", "555-0000",
                Role.TRAINER, "Yoga");
        assertFalse(r.isSuccess());
    }

    @Test
    void rejectsMemberRole() {
        Result r = controller.createStaff("tomt", "password1", "Trainer Tom", "tom@example.com", "555-0000",
                Role.MEMBER, "Yoga");
        assertFalse(r.isSuccess(), "member accounts must go through member registration, not staff creation");
    }

    @Test
    void rejectsInvalidEmail() {
        Result r = controller.createStaff("tomt", "password1", "Trainer Tom", "not-an-email", "555-0000",
                Role.TRAINER, "Yoga");
        assertFalse(r.isSuccess());
    }
}
