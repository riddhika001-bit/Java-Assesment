import java.sql.*;

/*
 * DBConnection.java
 * Gives a connection to MySQL using environment-provided credentials.
 */
public class DBConnection {

    static final String DB_NAME  = "hospital_management";
    private static final String USER_ENV = "HOSPITAL_DB_USER";
    private static final String PASSWORD_ENV = "HOSPITAL_DB_PASSWORD";

    private static final String OPTIONS = "?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/" + OPTIONS;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + OPTIONS;

    private static String getRequiredEnvironmentVariable(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new SQLException("Set the " + name + " environment variable before starting the application.");
        }
        return value;
    }

    // connects to MySQL server only (used to create the database)
    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(
                SERVER_URL,
                getRequiredEnvironmentVariable(USER_ENV),
                getRequiredEnvironmentVariable(PASSWORD_ENV));
    }

    // connects to hospital_management (used by everything else)
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DB_URL,
                getRequiredEnvironmentVariable(USER_ENV),
                getRequiredEnvironmentVariable(PASSWORD_ENV));
    }
}
