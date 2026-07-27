package view;

import controller.Result;
import controller.UserManagementController;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Admin "Manage Users" / "User Management" feature: list, add, suspend, activate, delete accounts. */
public class ManageUsersDialog extends JDialog {

    private final UserManagementController controller = new UserManagementController();
    private DefaultTableModel tableModel;
    private JTable table;

    public ManageUsersDialog(Window owner) {
        super(owner, "Manage Users", ModalityType.APPLICATION_MODAL);
        setSize(720, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Username", "Full Name", "Role", "Email", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAdd = new JButton("Add Staff Account");
        JButton btnSuspend = new JButton("Suspend");
        JButton btnActivate = new JButton("Activate");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");

        JPanel buttons = new JPanel();
        buttons.add(btnAdd);
        buttons.add(btnSuspend);
        buttons.add(btnActivate);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            new AddStaffDialog(owner).setVisible(true);
            refresh();
        });
        btnSuspend.addActionListener(e -> withSelectedUserId(id -> {
            Result r = controller.suspend(id);
            showAndRefresh(r);
        }));
        btnActivate.addActionListener(e -> withSelectedUserId(id -> {
            Result r = controller.activate(id);
            showAndRefresh(r);
        }));
        btnDelete.addActionListener(e -> withSelectedUserId(id -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this account permanently?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                showAndRefresh(controller.deleteUser(id));
            }
        }));
        btnRefresh.addActionListener(e -> refresh());
        btnClose.addActionListener(e -> dispose());

        refresh();
    }

    private void withSelectedUserId(java.util.function.IntConsumer action) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        action.accept(id);
    }

    private void showAndRefresh(Result r) {
        JOptionPane.showMessageDialog(this, r.getMessage());
        refresh();
    }

    private void refresh() {
        tableModel.setRowCount(0);
        List<User> users = controller.listAll();
        for (User u : users) {
            tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getFullName(), u.getRole(),
                    u.getEmail(), u.getStatus()});
        }
    }
}
