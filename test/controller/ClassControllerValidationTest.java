package controller;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;

/** Tests the validation branches of {@link ClassController#addClass}. */
class ClassControllerValidationTest {

    private final ClassController controller = new ClassController();

    @Test
    void rejectsBlankClassName() {
        Result r = controller.addClass(1, "", LocalDateTime.now().plusDays(1), 20);
        assertFalse(r.isSuccess());
    }

    @Test
    void rejectsPastScheduleTime() {
        Result r = controller.addClass(1, "Spin", LocalDateTime.now().minusDays(1), 20);
        assertFalse(r.isSuccess());
    }

    @Test
    void rejectsNonPositiveCapacity() {
        Result r = controller.addClass(1, "Spin", LocalDateTime.now().plusDays(1), 0);
        assertFalse(r.isSuccess());
    }
}
