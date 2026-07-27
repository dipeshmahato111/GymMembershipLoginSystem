package model;

import java.time.LocalDate;

/** Subscription that grants a member access to gym facilities. */
public class Membership {

    private int membershipId;
    private int memberId;
    private String tierName;
    private double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE, EXPIRED, SUSPENDED

    public Membership() {
    }

    public Membership(int membershipId, int memberId, String tierName, double price,
                       LocalDate startDate, LocalDate endDate, String status) {
        this.membershipId = membershipId;
        this.memberId = memberId;
        this.tierName = tierName;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public int getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * A membership grants access when its status is ACTIVE and today's date
     * has not passed the end date. Mirrors the {@code isActive()} operation
     * shown on the Membership class in Figure 6.
     */
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status) && endDate != null && !endDate.isBefore(LocalDate.now());
    }
}
