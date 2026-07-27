package view;

import controller.AuthResult;
import controller.AuthenticationController;
import model.Role;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Entry point screen implementing the "Member Login" use case (SRS 3.2.2).
 * On success, routes the user to the dashboard matching their role,
 * implementing the multi-tier RBAC described in SRS 2.2/4.5.
 *
 * <p>Styled with the system's purple brand theme ({@link UiTheme}) and a
 * programmatically-drawn logo ({@link GymLogoPanel}) per SRS 4.6
 * Usability (a consistent, standard color theme).</p>
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private final AuthenticationController authController = new AuthenticationController();

    public LoginFrame() {
        setTitle("Gym Membership Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setContentPane(buildBackgroundPanel());

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildBackgroundPanel() {
        JPanel background = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, UiTheme.PURPLE_DARK, 0, getHeight(), UiTheme.PURPLE_PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        background.setBorder(new EmptyBorder(35, 35, 35, 35));

        GridBagConstraints outer = new GridBagConstraints();
        outer.gridx = 0;
        outer.gridy = 0;
        outer.insets = new Insets(0, 0, 20, 0);
        background.add(buildLogoBlock(), outer);

        outer.gridy = 1;
        outer.insets = new Insets(0, 0, 0, 0);
        background.add(buildCard(), outer);

        return background;
    }

    private JPanel buildLogoBlock() {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));

        GymLogoPanel logo = new GymLogoPanel(90);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Gym Membership Login System");
        title.setFont(UiTheme.heading(20));
        title.setForeground(UiTheme.TEXT_LIGHT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Members, Trainers & Staff Portal");
        subtitle.setFont(UiTheme.body(12));
        subtitle.setForeground(UiTheme.TEXT_LIGHT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        block.add(logo);
        block.add(Box.createVerticalStrut(12));
        block.add(title);
        block.add(Box.createVerticalStrut(4));
        block.add(subtitle);
        return block;
    }

    private JPanel buildCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UiTheme.CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.PURPLE_LIGHT, 1, true),
                new EmptyBorder(25, 30, 25, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(16);
        txtPassword = new JPasswordField(16);
        styleField(txtUsername);
        styleField(txtPassword);

        int row = 0;
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(sectionLabel("Username"), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(txtUsername, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(sectionLabel("Password"), gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(txtPassword, gbc);

        JButton btnLogin = new JButton("Login");
        styleFilledButton(btnLogin, UiTheme.ACCENT_GOLD, UiTheme.PURPLE_DARK);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(btnLogin, gbc);

        JButton btnClear = new JButton("Clear");
        styleFlatButton(btnClear);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(btnClear, gbc);

        JButton btnRegister = new JButton("New Member? Sign Up");
        styleFlatButton(btnRegister);
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btnRegister, gbc);

        JLabel hint = new JLabel("<html><center>Default admin login: admin / Admin@123<br>"
                + "(please change this password after first login)</center></html>");
        hint.setFont(UiTheme.body(10));
        hint.setForeground(Color.GRAY);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = row++;
        gbc.insets = new Insets(16, 0, 0, 0);
        card.add(hint, gbc);

        btnLogin.addActionListener((ActionEvent e) -> login());
        txtPassword.addActionListener((ActionEvent e) -> login());

        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
        });

        btnRegister.addActionListener(e -> new RegisterMemberDialog(this, false).setVisible(true));

        return card;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiTheme.bodyBold(12));
        label.setForeground(UiTheme.PURPLE_DARK);
        return label;
    }

    private void styleField(JTextField field) {
        field.setFont(UiTheme.body(14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8D0E8), 1),
                new EmptyBorder(7, 10, 7, 10)));
    }

    private void styleFilledButton(JButton button, Color background, Color foreground) {
        button.setFont(UiTheme.bodyBold(14));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 10, 10, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleFlatButton(JButton button) {
        button.setFont(UiTheme.body(12));
        button.setForeground(UiTheme.PURPLE_PRIMARY);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
