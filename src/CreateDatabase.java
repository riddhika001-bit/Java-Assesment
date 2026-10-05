import java.sql.*;

/*
 * CreateDatabase.java
 * Creates the database "hospital_db" if it does not exist.
 */
public class CreateDatabase {

    public static boolean createDatabase() {
        String sql = "CREATE DATABASE IF NOT EXISTS " + DBConnection.DB_NAME;
        try (Connection con = DBConnection.getServerConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
            System.out.println("Database ready: " + DBConnection.DB_NAME);
            return true;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }
}