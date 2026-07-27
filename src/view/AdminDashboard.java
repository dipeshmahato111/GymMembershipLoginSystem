package view;

import model.User;

/**
 * Administrator landing screen. Admins have complete system management
 * privileges (SRS 2.3): manage users, configure membership pricing, and
 * generate operational reports.
 */
public class AdminDashboard extends DashboardFrame {

    public AdminDashboard(User user) {
        super("Admin Dashboard", user);

        addFeatureButton("Manage Users", () -> new ManageUsersDialog(this).setVisible(true));
        addFeatureButton("Membership Plans", () -> new MembershipPlansDialog(this).setVisible(true));
        addFeatureButton("Manage Members", () -> new ManageMembersDialog(this).setVisible(true));
        addFeatureButton("Register Member", () -> new RegisterMemberDialog(this, true).setVisible(true));
        addFeatureButton("Reports", () -> new ReportsDialog(this).setVisible(true));
        addFeatureButton("Payments History", () -> new PaymentsHistoryDialog(this).setVisible(true));

        finishLayout();
    }
}
