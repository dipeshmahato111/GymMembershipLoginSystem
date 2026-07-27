package view;

import model.User;

/**
 * Receptionist landing screen: registration, check-in, and payment
 * processing - the daily front-desk operations described in SRS 2.3.
 */
public class ReceptionistDashboard extends DashboardFrame {

    public ReceptionistDashboard(User user) {
        super("Receptionist Dashboard", user);

        addFeatureButton("Register Member", () -> new RegisterMemberDialog(this, true).setVisible(true));
        addFeatureButton("Check In", () -> new CheckInDialog(this).setVisible(true));
        addFeatureButton("Process Payment", () -> new ProcessPaymentDialog(this).setVisible(true));
        addFeatureButton("Manage Members", () -> new ManageMembersDialog(this).setVisible(true));

        finishLayout();
    }
}
