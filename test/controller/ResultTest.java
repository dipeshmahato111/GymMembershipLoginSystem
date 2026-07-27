package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void okFactoryProducesSuccessResult() {
        Result r = Result.ok("done");
        assertTrue(r.isSuccess());
        assertEquals("done", r.getMessage());
        assertEquals(-1, r.getGeneratedId());
    }

    @Test
    void okWithIdCarriesGeneratedId() {
        Result r = Result.ok("created", 42);
        assertTrue(r.isSuccess());
        assertEquals(42, r.getGeneratedId());
    }

    @Test
    void failFactoryProducesFailureResult() {
        Result r = Result.fail("nope");
        assertFalse(r.isSuccess());
        assertEquals("nope", r.getMessage());
    }
}
