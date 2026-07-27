package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Membership#isActive()}, the rule that gates
 * check-in (SRS 3.2.3) and class booking (SRS 3.2.4).
 */
class MembershipTest {

    @Test
    void activeStatusWithFutureEndDateIsActive() {
        Membership m = new Membership(1, 1, "Monthly", 39.99,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), "ACTIVE");
        assertTrue(m.isActive());
    }

    @Test
    void activeStatusWithPastEndDateIsNotActive() {
        Membership m = new Membership(1, 1, "Monthly", 39.99,
                LocalDate.now().minusMonths(2), LocalDate.now().minusDays(1), "ACTIVE");
        assertFalse(m.isActive(), "an expired end date should not be active even if status says ACTIVE");
    }

    @Test
    void suspendedStatusIsNeverActive() {
        Membership m = new Membership(1, 1, "Monthly", 39.99,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), "SUSPENDED");
        assertFalse(m.isActive());
    }

    @Test
    void endDateEqualToTodayIsStillActive() {
        Membership m = new Membership(1, 1, "Monthly", 39.99,
                LocalDate.now().minusDays(30), LocalDate.now(), "ACTIVE");
        assertTrue(m.isActive(), "membership should remain valid through its end date");
    }
}
