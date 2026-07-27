package view;

import controller.AuthResult;
import controller.AuthenticationController;
import model.Role;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Entry point screen implementing the "Member Login" use case (SRS 3.2.2).
 * On success, routes the user to the dashboard matching their role,
 * implementing the multi-tier RBAC described in SRS 2.2/4.5.
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final AuthenticationController authController = new AuthenticationController();

    public LoginFrame() {

        setTitle("Gym Membership Login System");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Gym Membership Login System");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);

        JButton btnLogin = new JButton("Login");
        JButton btnClear = new JButton("Clear");
        JButton btnRegister = new JButton("New Member? Sign Up");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(btnLogin, gbc);

        gbc.gridx = 1;
        panel.add(btnClear, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(btnRegister, gbc);

        JLabel hint = new JLabel("<html><center>Default admin login: admin / Admin@123<br>"
                + "(please change this password after first login)</center></html>");
        hint.setFont(new Font("Arial", Font.PLAIN, 10));
        hint.setForeground(Color.GRAY);
        gbc.gridy = 5;
        panel.add(hint, gbc);

        add(panel);

        btnLogin.addActionListener((ActionEvent e) -> login());
        txtPassword.addActionListener((ActionEvent e) -> login());

        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
        });

        btnRegister.addActionListener(e -> new RegisterMemberDialog(this, false).setVisible(true));

        setVisible(true);

    }

    private void login() {

        String username = txtUsername.getText();
        String password = String.valueOf(txtPassword.getPassword());

        AuthResult result = authController.login(username, password);

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + result.getUser().getFullName());
            dispose();
            routeToDashboard(result.getUser());
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }

    private void routeToDashboard(User user) {
        Role role = user.getRole();
        if (role == Role.ADMIN) {
            new AdminDashboard(user);
        } else if (role == Role.TRAINER) {
            new TrainerDashboard(user);
        } else if (role == Role.RECEPTIONIST) {
            new ReceptionistDashboard(user);
        } else {
            new MemberDashboard(user);
        }
    }
}
