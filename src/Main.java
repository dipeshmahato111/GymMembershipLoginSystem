import database.DbInitializer;
import view.LoginFrame;

/**
 * Application entry point. Seeds a default admin account / membership
 * tiers on first run, then shows the login screen (SRS 3.2.2).
 */
public class Main {
    public static void main(String[] args) {
        DbInitializer.initialize();
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
