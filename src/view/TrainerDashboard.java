package view;

import database.TrainerDAO;
import model.Trainer;
import model.User;

import javax.swing.*;

/**
 * Trainer landing screen: manage class schedule and view class rosters
 * (SRS 2.2 Trainer Scheduling).
 */
public class TrainerDashboard extends DashboardFrame {

    private final Trainer trainerProfile;

    public TrainerDashboard(User user) {
        super("Trainer Dashboard", user);

        Trainer profile;
        try {
            profile = new TrainerDAO().findByUserId(user.getUserId());
        } catch (Exception e) {
            profile = null;
        }
        this.trainerProfile = profile;

        addFeatureButton("My Classes", () -> new MyClassesDialog(this, trainerProfile).setVisible(true));
        addFeatureButton("View Class Roster", () -> new RosterDialog(this, trainerProfile).setVisible(true));

        finishLayout();

        if (trainerProfile == null) {
            JOptionPane.showMessageDialog(this,
                    "No trainer profile record found for this account. Ask an administrator to link it.",
                    "Setup Needed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
