package security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PasswordUtil}, verifying SRS 4.5's requirement
 * that passwords are hashed (never stored/compared in plaintext) using a
 * secure, salted algorithm.
 */
class PasswordUtilTest {

    @Test
    void hashProducesThreePartFormat() {
        String hash = PasswordUtil.hash("MySecret123");
        String[] parts = hash.split(":");
        assertEquals(3, parts.length, "hash should be iterations:salt:hash");
    }

    @Test
    void verifyReturnsTrueForCorrectPassword() {
        String hash = PasswordUtil.hash("CorrectHorseBatteryStaple");
        assertTrue(PasswordUtil.verify("CorrectHorseBatteryStaple", hash));
    }

    @Test
    void verifyReturnsFalseForWrongPassword() {
        String hash = PasswordUtil.hash("CorrectHorseBatteryStaple");
        assertFalse(PasswordUtil.verify("WrongPassword", hash));
    }

    @Test
    void sameInputProducesDifferentHashesDueToRandomSalt() {
        String hash1 = PasswordUtil.hash("SamePassword1");
        String hash2 = PasswordUtil.hash("SamePassword1");
        assertNotEquals(hash1, hash2, "each hash should use a fresh random salt");
        assertTrue(PasswordUtil.verify("SamePassword1", hash1));
        assertTrue(PasswordUtil.verify("SamePassword1", hash2));
    }

    @Test
    void verifyReturnsFalseForMalformedStoredHash() {
        assertFalse(PasswordUtil.verify("anything", "not-a-valid-hash"));
        assertFalse(PasswordUtil.verify("anything", null));
    }

    @Test
    void hashRejectsEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(""));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hash(null));
    }
}
