package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/** Tests the blank-input guard clauses of {@link AttendanceController}. */
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

    @Test
    void checkOutRejectsBlankIdentifier() {
        Result r = controller.checkOut("");
        assertFalse(r.isSuccess());
    }

    @Test
    void checkOutRejectsNullIdentifier() {
        Result r = controller.checkOut((String) null);
        assertFalse(r.isSuccess());
    }
}
