package model;

import java.time.LocalDateTime;

/** A transaction made for membership purchase or renewal. */
public class Payment {

    private int paymentId;
    private int membershipId;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String status; // COMPLETED, DECLINED, PENDING

    public Payment() {
    }

    public Payment(int paymentId, int membershipId, double amount, LocalDateTime paymentDate,
                    String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.membershipId = membershipId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
