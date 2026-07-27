package view;

import controller.MembershipController;
import model.Membership;
import model.User;

import javax.swing.*;
import java.awt.*;

/** Member "check their personal subscription status" feature (SRS 2.3). */
public class MyMembershipDialog extends JDialog {

    public MyMembershipDialog(Window owner, User user) {
        super(owner, "My Membership", ModalityType.APPLICATION_MODAL);
        setSize(420, 280);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        MembershipController controller = new MembershipController();
        Membership m = controller.getLatestMembership(user.getUserId());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (m == null) {
            area.setText("You do not have a membership on file yet.\nPlease see the front desk to purchase a plan.");
        } else {
            area.setText(String.format(
                    "Member ID:     %d%n"
                            + "Name:          %s%n"
                            + "Plan:          %s%n"
                            + "Price:         $%.2f%n"
                            + "Start Date:    %s%n"
                            + "End Date:      %s%n"
                            + "Status:        %s%n"
                            + "Access:        %s%n"
                            + "Digital ID:    MEM-%05d",
                    user.getUserId(), user.getFullName(), m.getTierName(), m.getPrice(),
                    m.getStartDate(), m.getEndDate(), m.getStatus(), m.isActive() ? "GRANTED" : "DENIED",
                    user.getUserId()));
        }

        add(area, BorderLayout.CENTER);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        JPanel buttons = new JPanel();
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);
    }
}
