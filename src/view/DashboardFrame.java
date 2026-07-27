package view;

import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Shared base class for all role-specific dashboards (Admin, Trainer,
 * Receptionist, Member). Provides the common header, "logged in as" banner,
 * feature button grid, and Logout action so each dashboard subclass only
 * has to declare which feature buttons it needs - keeping the UI
 * consistent across roles per SRS 4.6 Usability ("Navigation links and
 * core buttons must remain uniform and predictable across all dashboards").
 */
public abstract class DashboardFrame extends JFrame {

    protected final User currentUser;
    protected final JPanel contentPanel;

    protected DashboardFrame(String title, User user) {
        this.currentUser = user;

        setTitle("Gym Membership Login System - " + title);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 10));
        JLabel whoLabel = new JLabel("Logged in as: " + user.getFullName() + "  (" + user.getRole() + ")",
                SwingConstants.CENTER);
        whoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        headerPanel.add(titleLabel);
        headerPanel.add(whoLabel);
        add(headerPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        JPanel southPanel = new JPanel();
        southPanel.add(btnLogout);
        add(southPanel, BorderLayout.SOUTH);
    }

    /** Adds a feature button to the dashboard's grid. */
    protected void addFeatureButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());
        contentPanel.add(button);
    }

    /** Subclasses call this once all feature buttons have been added. */
    protected void finishLayout() {
        setVisible(true);
    }
}
