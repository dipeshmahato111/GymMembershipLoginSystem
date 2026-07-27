package controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the simulated payment gateway used by
 * {@link PaymentController}, exercising the "Payment declined" alternate
 * flow from SRS 3.2.5.
 */
class PaymentGatewaySimulatorTest {

    private final PaymentGatewaySimulator gateway = new PaymentGatewaySimulator();

    @Test
    void authorizesValidPositiveAmountWithMethod() {
        assertTrue(gateway.authorize(39.99, "Credit Card"));
    }

    @Test
    void declinesZeroOrNegativeAmount() {
        assertFalse(gateway.authorize(0, "Credit Card"));
        assertFalse(gateway.authorize(-10, "Credit Card"));
    }

    @Test
    void declinesMissingPaymentMethod() {
        assertFalse(gateway.authorize(39.99, null));
        assertFalse(gateway.authorize(39.99, "  "));
    }
}
