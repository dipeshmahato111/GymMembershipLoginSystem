package view;

import controller.MembershipController;
import controller.Result;
import model.MembershipTier;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Implements the "Register Member" use case (SRS 3.2.1). Reused both for
 * receptionist-assisted walk-in registration and member self sign-up from
 * the login screen (the wireframe in Figure 7 shows a "Sign Up" action).
 */
public class RegisterMemberDialog extends JDialog {

    private final MembershipController membershipController = new MembershipController();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<String> cmbTier;

    public RegisterMemberDialog(Window owner, boolean receptionistFlow) {
        super(owner, "Register Member", ModalityType.APPLICATION_MODAL);
        setSize(420, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(16);
        txtPassword = new JPasswordField(16);
        txtConfirm = new JPasswordField(16);
        txtFullName = new JTextField(16);
        txtEmail = new JTextField(16);
        txtPhone = new JTextField(16);

        List<MembershipTier> tiers = membershipController.listTiers();
        cmbTier = new JComboBox<>();
        for (MembershipTier t : tiers) {
            cmbTier.addItem(t.getTierName());
        }
        if (cmbTier.getItemCount() == 0) {
            cmbTier.addItem("Monthly");
        }

        int row = 0;
        row = addRow(form, gbc, row, "Username:", txtUsername);
        row = addRow(form, gbc, row, "Password:", txtPassword);
        row = addRow(form, gbc, row, "Confirm Password:", txtConfirm);
        row = addRow(form, gbc, row, "Full Name:", txtFullName);
        row = addRow(form, gbc, row, "Email:", txtEmail);
        row = addRow(form, gbc, row, "Phone:", txtPhone);
        row = addRow(form, gbc, row, "Membership Plan:", cmbTier);

        add(form, BorderLayout.CENTER);

        JButton btnSubmit = new JButton(receptionistFlow ? "Register Member" : "Sign Up");
        JButton btnCancel = new JButton("Cancel");
        JPanel buttons = new JPanel();
        buttons.add(btnSubmit);
        buttons.add(btnCancel);
        add(buttons, BorderLayout.SOUTH);

        btnCancel.addActionListener(e -> dispose());
        btnSubmit.addActionListener(e -> submit());

        setVisible(false);
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
        String password = String.valueOf(txtPassword.getPassword());
        String confirm = String.valueOf(txtConfirm.getPassword());
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Result result = membershipController.registerMember(
                txtUsername.getText(), password, txtFullName.getText(), txtEmail.getText(),
                txtPhone.getText(), (String) cmbTier.getSelectedItem());

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
