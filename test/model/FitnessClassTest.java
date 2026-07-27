package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Unit tests for {@link FitnessClass#getSeatsAvailable()} used by the booking flow (SRS 3.2.4). */
class FitnessClassTest {

    @Test
    void seatsAvailableIsCapacityMinusBookings() {
        FitnessClass fc = new FitnessClass(1, 1, "Yoga", LocalDateTime.now().plusDays(1), 20);
        fc.setCurrentBookings(14);
        assertEquals(6, fc.getSeatsAvailable());
    }

    @Test
    void seatsAvailableNeverGoesNegative() {
        FitnessClass fc = new FitnessClass(1, 1, "Yoga", LocalDateTime.now().plusDays(1), 10);
        fc.setCurrentBookings(15); // shouldn't normally happen, but must not underflow
        assertEquals(0, fc.getSeatsAvailable());
    }
}
