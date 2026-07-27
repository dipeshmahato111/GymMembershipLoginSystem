package view;

import controller.MembershipController;
import controller.PaymentController;
import controller.Result;
import database.UserDAO;
import model.Membership;
import model.Role;
import model.User;

import javax.swing.*;
import java.awt.*;

/** Implements the "Process Membership Payment" use case (SRS 3.2.5). */
public class ProcessPaymentDialog extends JDialog {

    private final MembershipController membershipController = new MembershipController();
    private final PaymentController paymentController = new PaymentController();
    private final UserDAO userDAO = new UserDAO();

    private JTextField txtMemberId;
    private JLabel lblInfo;
    private JComboBox<String> cmbMethod;

    public ProcessPaymentDialog(Window owner) {
        super(owner, "Process Membership Payment", ModalityType.APPLICATION_MODAL);
        setSize(420, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMemberId = new JTextField(14);
        JButton btnLookup = new JButton("Lookup");
        lblInfo = new JLabel(" ");
        cmbMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Bank Transfer"});

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Username or Member ID:"), gbc);
        gbc.gridx = 1;
        form.add(txtMemberId, gbc);
        gbc.gridx = 2;
        form.add(btnLookup, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        form.add(lblInfo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        form.add(cmbMethod, gbc);

        add(form, BorderLayout.CENTER);

        JButton btnPay = new JButton("Process Payment");
        JButton btnClose = new JButton("Close");
        JPanel buttons = new JPanel();
        buttons.add(btnPay);
        buttons.add(btnClose);
        add(buttons, BorderLayout.SOUTH);

        btnLookup.addActionListener(e -> lookup());
        btnPay.addActionListener(e -> pay());
        btnClose.addActionListener(e -> dispose());
    }

    private Membership lookup() {
        try {
            String text = txtMemberId.getText().trim();
            User user = text.matches("\\d+") ? userDAO.findById(Integer.parseInt(text)) : userDAO.findByUsername(text);
            if (user == null || user.getRole() != Role.MEMBER) {
                lblInfo.setText("No matching member found.");
                return null;
            }
            Membership m = membershipController.getLatestMembership(user.getUserId());
            if (m == null) {
                lblInfo.setText(user.getFullName() + " has no membership on file.");
                return null;
            }
            lblInfo.setText(String.format("<html>%s - %s plan, $%.2f, status: %s, expires %s</html>",
                    user.getFullName(), m.getTierName(), m.getPrice(), m.getStatus(), m.getEndDate()));
            return m;
        } catch (Exception ex) {
            lblInfo.setText("Lookup failed: " + ex.getMessage());
            return null;
        }
    }

    private void pay() {
        Membership m = lookup();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Look up a valid member first.");
            return;
        }
        Result result = paymentController.processPayment(m.getMembershipId(), (String) cmbMethod.getSelectedItem());
        JOptionPane.showMessageDialog(this, result.getMessage(),
                result.isSuccess() ? "Payment Receipt" : "Payment Declined",
                result.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (result.isSuccess()) {
            lookup();
        }
    }
}
