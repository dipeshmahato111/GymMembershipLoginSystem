package controller;

import database.MembershipDAO;
import database.PaymentDAO;
import model.Membership;
import model.Payment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Implements the "Process Membership Payment" use case (SRS 3.2.5). */
public class PaymentController {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final PaymentGatewaySimulator gateway = new PaymentGatewaySimulator();

    /**
     * Processes a payment for the given membership: authorizes via the
     * (simulated) payment gateway, records the transaction, and on success
     * extends/activates the membership.
     */
    public Result processPayment(int membershipId, String paymentMethod) {
        try {
            Membership membership = membershipDAO.findById(membershipId);
            if (membership == null) {
                return Result.fail("Membership not found.");
            }
            double amount = membership.getPrice();

            boolean authorized = gateway.authorize(amount, paymentMethod);
            Payment payment = new Payment(0, membershipId, amount, LocalDateTime.now(), paymentMethod,
                    authorized ? "COMPLETED" : "DECLINED");
            int paymentId = paymentDAO.insert(payment);

            if (!authorized) {
                return Result.fail("Payment declined by gateway. Please try a different payment method.");
            }

            LocalDate base = membership.getEndDate().isBefore(LocalDate.now())
                    ? LocalDate.now() : membership.getEndDate();
            membership.setEndDate(base.plusMonths(1));
            membership.setStatus("ACTIVE");
            membershipDAO.update(membership);

            return Result.ok(generateReceipt(paymentId), paymentId);
        } catch (SQLException e) {
            return Result.fail("Payment processing failed - database unavailable. " + e.getMessage());
        }
    }

    public String generateReceipt(int paymentId) {
        try {
            Payment p = paymentDAO.findById(paymentId);
            if (p == null) {
                return "Receipt unavailable.";
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
            return String.format(
                    "===== PAYMENT RECEIPT =====%n"
                            + "Receipt #: %d%n"
                            + "Date: %s%n"
                            + "Amount: $%.2f%n"
                            + "Method: %s%n"
                            + "Status: %s%n"
                            + "============================",
                    p.getPaymentId(), p.getPaymentDate().format(fmt), p.getAmount(),
                    p.getPaymentMethod(), p.getStatus());
        } catch (SQLException e) {
            return "Receipt unavailable: " + e.getMessage();
        }
    }

    public List<Payment> historyForMembership(int membershipId) {
        try {
            return paymentDAO.findByMembership(membershipId);
        } catch (SQLException e) {
            return List.of();
        }
    }

    public List<Payment> allPayments() {
        try {
            return paymentDAO.findAll();
        } catch (SQLException e) {
            return List.of();
        }
    }

    public double revenueThisMonth() {
        try {
            LocalDate now = LocalDate.now();
            LocalDate start = now.withDayOfMonth(1);
            return paymentDAO.sumRevenueBetween(start, now);
        } catch (SQLException e) {
            return 0.0;
        }
    }
}
