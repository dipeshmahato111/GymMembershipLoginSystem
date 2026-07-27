package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/** Tests the blank-input guard clause of {@link AttendanceController#checkIn}. */
class AttendanceControllerValidationTest {

    private final AttendanceController controller = new AttendanceController();

    @Test
    void rejectsBlankIdentifier() {
        Result r = controller.checkIn("");
        assertFalse(r.isSuccess());
    }

    @Test
    void rejectsNullIdentifier() {
        Result r = controller.checkIn(null);
        assertFalse(r.isSuccess());
    }
}
