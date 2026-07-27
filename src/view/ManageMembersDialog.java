package view;

import controller.MembershipController;
import controller.Result;
import model.Membership;
import model.Role;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Shared "Manage Members" feature (used by both Receptionist and Admin
 * dashboards): lists members with their current membership status, and
 * lets staff renew or suspend a membership.
 */
public class ManageMembersDialog extends JDialog {

    private final MembershipController membershipController = new MembershipController();
    private DefaultTableModel tableModel;
    private JTable table;
    private List<User> members;

    public ManageMembersDialog(Window owner) {
        super(owner, "Manage Members", ModalityType.APPLICATION_MODAL);
        setSize(760, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(
                new Object[]{"Member ID", "Name", "Username", "Plan", "Status", "Expires"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnRenew = new JButton("Renew 1 Month");
        JButton btnSuspend = new JButton("Suspend Membership");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");

        JPanel buttons = new JPanel();
        buttons.add(btnRenew);
        buttons.add(btnSuspend);
        buttons.add(btnRefresh);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnRenew.addActionListener(e -> withSelectedMembership(m -> {
            Result r = membershipController.renewMembership(m.getMembershipId(), 1);
            JOptionPane.showMessageDialog(this, r.getMessage());
            refresh();
        }));
        btnSuspend.addActionListener(e -> withSelectedMembership(m -> {
            Result r = membershipController.suspendMembership(m.getMembershipId());
            JOptionPane.showMessageDialog(this, r.getMessage());
            refresh();
        }));
        btnRefresh.addActionListener(e -> refresh());
        btnClose.addActionListener(e -> dispose());

        refresh();
    }

    private void withSelectedMembership(java.util.function.Consumer<Membership> action) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a member first.");
            return;
        }
        int memberId = (int) tableModel.getValueAt(row, 0);
        Membership m = membershipController.getLatestMembership(memberId);
        if (m == null) {
            JOptionPane.showMessageDialog(this, "This member has no membership record.");
            return;
        }
        action.accept(m);
    }

    private void refresh() {
        tableModel.setRowCount(0);
        try {
            members = new database.UserDAO().findByRole(Role.MEMBER);
        } catch (Exception ex) {
            members = List.of();
        }
        for (User u : members) {
            Membership m = membershipController.getLatestMembership(u.getUserId());
            tableModel.addRow(new Object[]{
                    u.getUserId(), u.getFullName(), u.getUsername(),
                    m == null ? "-" : m.getTierName(),
                    m == null ? "NONE" : m.getStatus(),
                    m == null ? "-" : m.getEndDate()
            });
        }
    }
}
