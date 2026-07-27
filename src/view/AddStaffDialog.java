package view;

import controller.Result;
import controller.UserManagementController;
import model.Role;

import javax.swing.*;
import java.awt.*;

/** Admin form to create a Trainer, Receptionist, or Administrator account. */
public class AddStaffDialog extends JDialog {

    private final UserManagementController controller = new UserManagementController();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<Role> cmbRole;
    private JTextField txtExtra;
    private JLabel lblExtra;

    public AddStaffDialog(Window owner) {
        super(owner, "Add Staff Account", ModalityType.APPLICATION_MODAL);
        setSize(400, 380);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(16);
        txtPassword = new JPasswordField(16);
        txtFullName = new JTextField(16);
        txtEmail = new JTextField(16);
        txtPhone = new JTextField(16);
        cmbRole = new JComboBox<>(new Role[]{Role.TRAINER, Role.RECEPTIONIST, Role.ADMIN});
        txtExtra = new JTextField(16);
        lblExtra = new JLabel("Specialization:");

        cmbRole.addActionListener(e -> updateExtraLabel());
        updateExtraLabel();

        int row = 0;
        row = addRow(form, gbc, row, "Username:", txtUsername);
        row = addRow(form, gbc, row, "Password:", txtPassword);
        row = addRow(form, gbc, row, "Full Name:", txtFullName);
        row = addRow(form, gbc, row, "Email:", txtEmail);
        row = addRow(form, gbc, row, "Phone:", txtPhone);
        row = addRow(form, gbc, row, "Role:", cmbRole);
        row = addRow(form, gbc, row, lblExtra.getText(), txtExtra);

        add(form, BorderLayout.CENTER);

        JButton btnSave = new JButton("Create Account");
        JButton btnCancel = new JButton("Cancel");
        JPanel buttons = new JPanel();
        buttons.add(btnSave);
        buttons.add(btnCancel);
        add(buttons, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> submit());
    }

    private void updateExtraLabel() {
        Role role = (Role) cmbRole.getSelectedItem();
        if (role == Role.TRAINER) {
            lblExtra.setText("Specialization:");
        } else if (role == Role.RECEPTIONIST) {
            lblExtra.setText("Shift:");
        } else {
            lblExtra.setText("Admin Level:");
        }
    }

    private int addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(field, gbc);
        return row + 1;
    }

    private void submit() {
        Role role = (Role) cmbRole.getSelectedItem();
        Result result = controller.createStaff(txtUsername.getText(),
                String.valueOf(txtPassword.getPassword()), txtFullName.getText(), txtEmail.getText(),
                txtPhone.getText(), role, txtExtra.getText());

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
