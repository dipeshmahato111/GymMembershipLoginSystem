package view;

import controller.AttendanceController;
import controller.Result;
import model.User;

import javax.swing.*;

/**
 * Member landing screen: profile/membership status, class booking,
 * self check-in, and attendance history (SRS 2.4 Member View).
 */
public class MemberDashboard extends DashboardFrame {

    private final AttendanceController attendanceController = new AttendanceController();

    public MemberDashboard(User user) {
        super("Member Dashboard", user);

        addFeatureButton("My Membership", () -> new MyMembershipDialog(this, user).setVisible(true));
        addFeatureButton("Fitness Classes", () -> new FitnessClassesDialog(this, user).setVisible(true));
        addFeatureButton("Check In", this::selfCheckIn);
        addFeatureButton("Check Out", this::selfCheckOut);
        addFeatureButton("Check-In History", () -> new CheckInHistoryDialog(this, user).setVisible(true));

        finishLayout();
    }

    private void selfCheckIn() {
        Result result = attendanceController.checkIn(String.valueOf(currentUser.getUserId()));
        JOptionPane.showMessageDialog(this, result.getMessage(),
                result.isSuccess() ? "Check-In" : "Check-In Failed",
                result.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    private void selfCheckOut() {
        Result result = attendanceController.checkOut(String.valueOf(currentUser.getUserId()));
        JOptionPane.showMessageDialog(this, result.getMessage(),
                result.isSuccess() ? "Check-Out" : "Check-Out Failed",
                result.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
}
