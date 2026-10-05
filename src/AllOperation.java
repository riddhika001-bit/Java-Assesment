import java.sql.*;
import java.util.*;

/*
 * AllOperation.java
 * All hospital operations (database + DSA).
 *
 * DSA used:
 *  1. ArrayList       -> list of patients fetched from the database
 *  2. PriorityQueue   -> treatment queue (most critical patient first)
 *  3. HashSet         -> quickly check if a patient is already in the queue
 *  4. Stack           -> undo the last discharge
 *  5. Bubble Sort     -> sort patients by name
 *  6. Linear Search   -> search patients by name
 *  7. Queue + HashMap -> SMART DOCTOR ASSIGNMENT: disease -> department (HashMap),
 *                        doctors of a department rotate in a Queue (round-robin)
 */
public class AllOperation {

    // ---------- Patient class ----------
    static class Patient {
        int id;
        String name;
        int age;
        String disease;
        int severity;        // 1 = Critical, 2 = Moderate, 3 = Normal

        Patient(int id, String name, int age, String disease, int severity) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.disease = disease;
            this.severity = severity;
        }

        String severityText() {
            if (severity == 1) return "Critical";
            if (severity == 2) return "Moderate";
            return "Normal";
        }

        void display() {
            System.out.printf("ID: %-4d | Name: %-15s | Age: %-3d | Disease: %-12s | Severity: %-8s%n",
                    id, name, age, disease, severityText());
        }
    }

    // ---------- Doctor class ----------
    static class Doctor {
        int id;
        String name;
        String department;
        int patientsTreated;

        Doctor(int id, String name, String department, int patientsTreated) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.patientsTreated = patientsTreated;
        }
    }

    // ---------- Data structures ----------
    static Scanner sc = new Scanner(System.in);

    // lower severity number = more urgent; same severity -> smaller ID first
    static PriorityQueue<Patient> treatmentQueue = new PriorityQueue<>(
            (a, b) -> a.severity != b.severity ? a.severity - b.severity : a.id - b.id);
    static HashSet<Integer> queuedIds = new HashSet<>();
    static Stack<Patient> dischargeHistory = new Stack<>();

    static HashMap<String, String> keywordToDept = new HashMap<>();
    static HashMap<String, Queue<Doctor>> deptDoctors = new HashMap<>();

    // ---------- Input helpers ----------
    public static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    static String readText(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("This cannot be empty!");
        }
    }

    static int readAge() {
        while (true) {
            int age = readInt("Enter age: ");
            if (age > 0 && age < 120) return age;
            System.out.println("Enter a valid age (1-119)!");
        }
    }

    static int readSeverity() {
        int s = readInt("Severity (1=Critical, 2=Moderate, 3=Normal): ");
        if (s < 1 || s > 3) {
            System.out.println("Invalid severity! Setting to 3 (Normal).");
            return 3;
        }
        return s;
    }

    // ---------- Database helpers ----------
    static Patient rowToPatient(ResultSet rs) throws SQLException {
        return new Patient(rs.getInt("id"), rs.getString("name"), rs.getInt("age"),
                rs.getString("disease"), rs.getInt("severity"));
    }

    static ArrayList<Patient> fetchAllPatients() {
        ArrayList<Patient> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM patients ORDER BY id")) {
            while (rs.next()) list.add(rowToPatient(rs));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return list;
    }

    static Patient getPatientById(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM patients WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rowToPatient(rs);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    // ---------- Smart Doctor Assignment setup ----------
    public static void setupDoctors() {
        // disease keyword -> department (anything else goes to General Medicine)
        keywordToDept.put("heart", "Cardiology");
        keywordToDept.put("chest", "Cardiology");
        keywordToDept.put("bp", "Cardiology");
        keywordToDept.put("fracture", "Orthopedics");
        keywordToDept.put("bone", "Orthopedics");
        keywordToDept.put("accident", "Orthopedics");
        keywordToDept.put("skin", "Dermatology");
        keywordToDept.put("rash", "Dermatology");
        keywordToDept.put("allergy", "Dermatology");

        // load doctors from database into a queue per department
        deptDoctors.clear();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM doctors ORDER BY id")) {
            while (rs.next()) {
                Doctor d = new Doctor(rs.getInt("id"), rs.getString("name"),
                        rs.getString("department"), rs.getInt("patients_treated"));
                deptDoctors.putIfAbsent(d.department, new LinkedList<>());
                deptDoctors.get(d.department).add(d);
            }
        } catch (SQLException e) {
            System.out.println("Error loading doctors: " + e.getMessage());
        }
    }

    static String findDepartment(String disease) {
        String d = disease.toLowerCase();
        for (Map.Entry<String, String> e : keywordToDept.entrySet()) {
            if (d.contains(e.getKey())) return e.getValue();
        }
        return "General Medicine";
    }

    static Doctor assignDoctor(Patient p) {
        Queue<Doctor> q = deptDoctors.get(findDepartment(p.disease));
        if (q == null || q.isEmpty()) return null;

        Doctor doc = q.poll();          // doctor at the front
        doc.patientsTreated++;
        q.add(doc);                     // goes to the back -> round-robin

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE doctors SET patients_treated = patients_treated + 1 WHERE id = ?")) {
            ps.setInt(1, doc.id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return doc;
    }

    // ---------- Features ----------
    public static void addPatient() {
        String name = readText("Enter name: ");
        int age = readAge();
        String disease = readText("Enter disease: ");
        int severity = readSeverity();

        String sql = "INSERT INTO patients (name, age, disease, severity) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, disease);
            ps.setInt(4, severity);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) System.out.println("Patient added! Patient ID = " + keys.getInt(1));
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewAll() {
        ArrayList<Patient> list = fetchAllPatients();
        if (list.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        System.out.println("\n--- All Patients ---");
        for (Patient p : list) p.display();
    }

    public static void searchById() {
        int id = readInt("Enter patient ID: ");
        Patient p = getPatientById(id);
        if (p == null) System.out.println("Patient not found.");
        else p.display();
    }

    public static void searchByName() {
        String name = readText("Enter name to search: ").toLowerCase();
        boolean found = false;
        for (Patient p : fetchAllPatients()) {            // Linear search
            if (p.name.toLowerCase().contains(name)) {
                p.display();
                found = true;
            }
        }
        if (!found) System.out.println("Patient not found.");
    }

    public static void updatePatient() {
        int id = readInt("Enter patient ID to update: ");
        Patient p = getPatientById(id);
        if (p == null) {
            System.out.println("Patient not found.");
            return;
        }
        p.display();
        p.disease = readText("Enter new disease: ");
        p.severity = readSeverity();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE patients SET disease = ?, severity = ? WHERE id = ?")) {
            ps.setString(1, p.disease);
            ps.setInt(2, p.severity);
            ps.setInt(3, id);
            ps.executeUpdate();
            System.out.println("Patient updated.");

            // if patient is waiting in the queue, re-insert so the priority updates
            if (queuedIds.contains(id)) {
                treatmentQueue.removeIf(x -> x.id == id);
                treatmentQueue.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void sortByName() {
        ArrayList<Patient> list = fetchAllPatients();
        if (list.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        // Bubble sort
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).name.compareToIgnoreCase(list.get(j + 1).name) > 0) {
                    Patient temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
        System.out.println("\n--- Patients Sorted by Name ---");
        for (Patient p : list) p.display();
    }

    public static void addToQueue() {
        int id = readInt("Enter patient ID to add to treatment queue: ");
        Patient p = getPatientById(id);
        if (p == null) {
            System.out.println("Patient not found.");
        } else if (queuedIds.contains(id)) {
            System.out.println("Patient is already in the queue.");
        } else {
            treatmentQueue.add(p);
            queuedIds.add(id);
            System.out.println(p.name + " added to treatment queue.");
        }
    }

    public static void treatNext() {
        Patient p = treatmentQueue.poll();                // most critical first
        if (p == null) {
            System.out.println("No patients waiting in the queue.");
            return;
        }
        queuedIds.remove(p.id);
        Doctor doc = assignDoctor(p);

        System.out.println("Now treating:");
        p.display();
        if (doc == null) System.out.println("No doctor available for this department.");
        else System.out.println("Auto-assigned to " + doc.name + " (" + doc.department + ")");
    }

    public static void viewQueue() {
        if (treatmentQueue.isEmpty()) {
            System.out.println("Treatment queue is empty.");
            return;
        }
        System.out.println("\n--- Treatment Queue (in order) ---");
        PriorityQueue<Patient> copy = new PriorityQueue<>(treatmentQueue);  // keep original safe
        int pos = 1;
        while (!copy.isEmpty()) {
            System.out.print(pos++ + ". ");
            copy.poll().display();
        }
    }

    public static void dischargePatient() {
        int id = readInt("Enter patient ID to discharge: ");
        Patient p = getPatientById(id);
        if (p == null) {
            System.out.println("Patient not found.");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM patients WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();

            if (queuedIds.contains(id)) {
                treatmentQueue.removeIf(x -> x.id == id);
                queuedIds.remove(id);
            }
            dischargeHistory.push(p);                     // save for undo
            System.out.println(p.name + " has been discharged.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void undoDischarge() {
        if (dischargeHistory.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        Patient p = dischargeHistory.pop();               // last discharged patient
        String sql = "INSERT INTO patients (id, name, age, disease, severity) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.id);
            ps.setString(2, p.name);
            ps.setInt(3, p.age);
            ps.setString(4, p.disease);
            ps.setInt(5, p.severity);
            ps.executeUpdate();
            System.out.println("Undo successful. " + p.name + " is back in the system.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewDoctors() {
        System.out.println("\n--- Doctors & Workload ---");
        for (String dept : deptDoctors.keySet()) {
            for (Doctor d : deptDoctors.get(dept)) {
                System.out.printf("%-10s | %-17s | Patients treated: %d%n",
                        d.name, d.department, d.patientsTreated);
            }
        }
    }
}
