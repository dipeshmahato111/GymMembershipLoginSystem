package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central place that opens JDBC connections to the MySQL database
 * (SRS 2.6: "The system shall use a MySQL relational database" / "JDBC
 * shall provide database connectivity").
 *
 * <p>Connection settings are externalized to {@code resources/db.properties}
 * (falling back to environment variables, then sane local defaults) instead
 * of being hard-coded, so credentials never need to live in source code and
 * facility configuration can be changed without a rebuild (SRS 4.4).</p>
 */
public class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/gym_management";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final Properties PROPERTIES = loadProperties();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver may already be auto-registered by newer JDBC drivers via SPI;
            // ignore so getConnection() can still surface a clearer SQLException.
        }
    }

    private DatabaseConnection() {
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        String[] candidatePaths = {"/db.properties", "/resources/db.properties"};
        for (String path : candidatePaths) {
            try (InputStream in = DatabaseConnection.class.getResourceAsStream(path)) {
                if (in != null) {
                    props.load(in);
                    break;
                }
            } catch (IOException ignored) {
                // try next candidate path
            }
        }
        return props;
    }

    private static String resolve(String propertyKey, String envVar, String fallback) {
        String value = PROPERTIES.getProperty(propertyKey);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        String env = System.getenv(envVar);
        if (env != null && !env.isEmpty()) {
            return env;
        }
        return fallback;
    }

    public static Connection getConnection() throws SQLException {
        String url = resolve("db.url", "DB_URL", DEFAULT_URL);
        String user = resolve("db.user", "DB_USER", DEFAULT_USER);
        String password = resolve("db.password", "DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }
}
