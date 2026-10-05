import java.sql.*;

/*
 * CreateTable.java
 * Creates the "patients" and "doctors" tables and adds default doctors.
 */
public class CreateTable {

    public static boolean createTables() {
        String patientsTable =
                "CREATE TABLE IF NOT EXISTS patients (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL, " +
                "age INT NOT NULL, " +
                "disease VARCHAR(50) NOT NULL, " +
                "severity INT NOT NULL" +
                ") AUTO_INCREMENT = 101";

        String doctorsTable =
                "CREATE TABLE IF NOT EXISTS doctors (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL, " +
                "department VARCHAR(50) NOT NULL, " +
                "patients_treated INT DEFAULT 0" +
                ")";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {

            st.executeUpdate(patientsTable);
            st.executeUpdate(doctorsTable);

            // add default doctors only if the table is empty
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM doctors");
            rs.next();
            if (rs.getInt(1) == 0) {
                st.executeUpdate("INSERT INTO doctors (name, department) VALUES " +
                        "('Dr. Sharma', 'General Medicine'), " +
                        "('Dr. Patil', 'General Medicine'), " +
                        "('Dr. Rao', 'Cardiology'), " +
                        "('Dr. Khan', 'Orthopedics'), " +
                        "('Dr. Mehta', 'Orthopedics'), " +
                        "('Dr. Nair', 'Dermatology')");
                System.out.println("Default doctors added.");
            }
            System.out.println("Tables ready.");
            return true;
        } catch (SQLException e) {
            System.out.println("Table error: " + e.getMessage());
            return false;
        }
    }
}
