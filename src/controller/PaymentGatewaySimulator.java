package controller;

/**
 * Stand-in for the external "Payment Gateway" actor shown in the SRS use
 * case (Figure 1) and sequence diagram (Figure 4). A class project has no
 * access to a real merchant account, so authorization is simulated here
 * behind the same interface a real gateway integration would expose -
 * swapping in a real provider later only requires replacing this class.
 */
public class PaymentGatewaySimulator {

    /**
     * "Authorizes" a transaction. Declines only on obviously invalid input
     * (used to exercise the Payment Declined alternate flow from SRS 3.2.5).
     */
    public boolean authorize(double amount, String paymentMethod) {
        if (amount <= 0) {
            return false;
        }
        return paymentMethod != null && !paymentMethod.isBlank();
    }
}
