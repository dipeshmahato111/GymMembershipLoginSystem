package model;

/**
 * An admin-configurable membership pricing tier (e.g. Monthly, Annual, VIP).
 * Stored in the database (not hard-coded) so rates can be updated from the
 * Admin Dashboard without code changes, per SRS 4.4 Maintainability.
 */
public class MembershipTier {

    private int tierId;
    private String tierName;
    private double price;
    private int durationMonths;

    public MembershipTier() {
    }

    public MembershipTier(int tierId, String tierName, double price, int durationMonths) {
        this.tierId = tierId;
        this.tierName = tierName;
        this.price = price;
        this.durationMonths = durationMonths;
    }

    public int getTierId() {
        return tierId;
    }

    public void setTierId(int tierId) {
        this.tierId = tierId;
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

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    @Override
    public String toString() {
        return tierName + " - $" + price + " / " + durationMonths + " mo";
    }
}
