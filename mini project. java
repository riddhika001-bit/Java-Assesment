import java.util.*;

/*
 * Hospital / Patient Management System (Simple College Project)
 * ---------------------------------------------------------------
 * DSA used:
 *  1. ArrayList      -> stores all patient records
 *  2. HashMap        -> fast search of a patient by ID  (O(1))
 *  3. PriorityQueue  -> treatment queue (most critical patient first)
 *  4. Stack          -> undo the last discharge
 *  5. Bubble Sort    -> sort patients by name
 *  6. Linear Search  -> search patients by name
 *  7. Queue + HashMap -> SMART DOCTOR ASSIGNMENT (new feature):
 *       disease is auto-mapped to a department (HashMap) and each
 *       department keeps its doctors in a Queue, so patients are
 *       assigned fairly in round-robin order (workload balancing).
 *
 * Run:  javac HospitalManagementSystem.java
 *       java HospitalManagementSystem
 */
public class HospitalManagementSystem {

    // ---------- Patient class ----------
    static class Patient {
        int id;
        String name;
        int age;
        String disease;
        int severity;        // 1 = Critical, 2 = Moderate, 3 = Normal
        boolean inQueue;     // is the patient waiting for treatment?

        Patient(int id, String name, int age, String disease, int severity) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.disease = disease;
            this.severity = severity;
            this.inQueue = false;
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
        String name;
        String department;
        int patientsTreated;

        Doctor(String name, String department) {
            this.name = name;
            this.department = department;
            this.patientsTreated = 0;
        }
    }

    // ---------- Data structures ----------
    static ArrayList<Patient> patients = new ArrayList<>();
    static HashMap<Integer, Patient> patientMap = new HashMap<>();
    static Stack<Patient> dischargeHistory = new Stack<>();

    // Lower severity number = more urgent. If same severity, smaller ID goes first.
    static PriorityQueue<Patient> treatmentQueue = new PriorityQueue<>(
            (a, b) -> a.severity != b.severity ? a.severity - b.severity : a.id - b.id);

    // Smart doctor assignment: keyword -> department, department -> queue of doctors
    static HashMap<String, String> keywordToDept = new HashMap<>();
    static HashMap<String, Queue<Doctor>> deptDoctors = new HashMap<>();

    static Scanner sc = new Scanner(System.in);
    static int nextId = 101;

    // ---------- Input helper ----------
    static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    // ---------- Smart Doctor Assignment ----------
    static void addDoctor(String name, String dept) {
        deptDoctors.putIfAbsent(dept, new LinkedList<>());
        deptDoctors.get(dept).add(new Doctor(name, dept));
    }

    static void setupDoctors() {
        // doctors (each department has a queue of doctors)
        addDoctor("Dr. Sharma", "General Medicine");
        addDoctor("Dr. Patil", "General Medicine");
        addDoctor("Dr. Rao", "Cardiology");
        addDoctor("Dr. Khan", "Orthopedics");
        addDoctor("Dr. Mehta", "Orthopedics");
        addDoctor("Dr. Nair", "Dermatology");

        // disease keyword -> department
        keywordToDept.put("heart", "Cardiology");
        keywordToDept.put("chest", "Cardiology");
        keywordToDept.put("bp", "Cardiology");
        keywordToDept.put("fracture", "Orthopedics");
        keywordToDept.put("bone", "Orthopedics");
        keywordToDept.put("accident", "Orthopedics");
        keywordToDept.put("skin", "Dermatology");
        keywordToDept.put("rash", "Dermatology");
        keywordToDept.put("allergy", "Dermatology");
        // anything else (fever, cold, flu...) goes to General Medicine
    }

    static String findDepartment(String disease) {
        String d = disease.toLowerCase();
        for (Map.Entry<String, String> e : keywordToDept.entrySet()) {
            if (d.contains(e.getKey())) return e.getValue();
        }
        return "General Medicine";
    }

    static Doctor assignDoctor(Patient p) {
        String dept = findDepartment(p.disease);
        Queue<Doctor> q = deptDoctors.get(dept);
        Doctor doc = q.poll();      // doctor at the front of the queue
        doc.patientsTreated++;
        q.add(doc);                 // goes to the back -> round-robin
        return doc;
    }

    static void viewDoctors() {
        System.out.println("\n--- Doctors & Workload ---");
        for (String dept : deptDoctors.keySet()) {
            for (Doctor d : deptDoctors.get(dept)) {
                System.out.printf("%-10s | %-17s | Patients treated: %d%n",
                        d.name, d.department, d.patientsTreated);
            }
        }
    }

    // ---------- Features ----------
    static void addPatient() {
        System.out.print("Enter name: ");
        String name = sc.nextLine().trim();
        int age = readInt("Enter age: ");
        System.out.print("Enter disease: ");
        String disease = sc.nextLine().trim();
        int severity = readInt("Severity (1=Critical, 2=Moderate, 3=Normal): ");
        if (severity < 1 || severity > 3) {
            System.out.println("Invalid severity! Setting to 3 (Normal).");
            severity = 3;
        }

        Patient p = new Patient(nextId++, name, age, disease, severity);
        patients.add(p);
        patientMap.put(p.id, p);
        System.out.println("Patient added successfully! Patient ID = " + p.id);
    }

    static void viewAll() {
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        System.out.println("\n--- All Patients ---");
        for (Patient p : patients) p.display();
    }

    static void searchById() {
        int id = readInt("Enter patient ID: ");
        Patient p = patientMap.get(id);          // HashMap lookup
        if (p == null) System.out.println("Patient not found.");
        else p.display();
    }

    static void searchByName() {
        System.out.print("Enter name to search: ");
        String name = sc.nextLine().trim();
        boolean found = false;
        for (Patient p : patients) {             // Linear search
            if (p.name.equalsIgnoreCase(name)) {
                p.display();
                found = true;
            }
        }
        if (!found) System.out.println("Patient not found.");
    }

    static void sortByName() {
        // Bubble sort on the ArrayList
        int n = patients.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (patients.get(j).name.compareToIgnoreCase(patients.get(j + 1).name) > 0) {
                    Patient temp = patients.get(j);
                    patients.set(j, patients.get(j + 1));
                    patients.set(j + 1, temp);
                }
            }
        }
        System.out.println("Patients sorted by name.");
        viewAll();
    }

    static void addToQueue() {
        int id = readInt("Enter patient ID to add to treatment queue: ");
        Patient p = patientMap.get(id);
        if (p == null) {
            System.out.println("Patient not found.");
        } else if (p.inQueue) {
            System.out.println("Patient is already in the queue.");
        } else {
            treatmentQueue.add(p);
            p.inQueue = true;
            System.out.println(p.name + " added to treatment queue.");
        }
    }

    static void treatNext() {
        Patient p = treatmentQueue.poll();       // most critical first
        if (p == null) {
            System.out.println("No patients waiting in the queue.");
        } else {
            p.inQueue = false;
            Doctor doc = assignDoctor(p);
            System.out.println("Now treating: ");
            p.display();
            System.out.println("Auto-assigned to " + doc.name + " (" + doc.department + ")");
        }
    }

    static void viewQueue() {
        if (treatmentQueue.isEmpty()) {
            System.out.println("Treatment queue is empty.");
            return;
        }
        System.out.println("\n--- Treatment Queue (in order) ---");
        // copy the queue so the original is not disturbed
        PriorityQueue<Patient> copy = new PriorityQueue<>(treatmentQueue);
        int pos = 1;
        while (!copy.isEmpty()) {
            System.out.print(pos++ + ". ");
            copy.poll().display();
        }
    }

    static void dischargePatient() {
        int id = readInt("Enter patient ID to discharge: ");
        Patient p = patientMap.get(id);
        if (p == null) {
            System.out.println("Patient not found.");
            return;
        }
        patients.remove(p);
        patientMap.remove(id);
        if (p.inQueue) {
            treatmentQueue.remove(p);
            p.inQueue = false;
        }
        dischargeHistory.push(p);                // save for undo
        System.out.println(p.name + " has been discharged.");
    }

    static void undoDischarge() {
        if (dischargeHistory.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }
        Patient p = dischargeHistory.pop();      // last discharged patient
        patients.add(p);
        patientMap.put(p.id, p);
        System.out.println("Undo successful. " + p.name + " is back in the system.");
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        setupDoctors();
        int choice;
        do {
            System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
            System.out.println("1. Add Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Search Patient by ID");
            System.out.println("4. Search Patient by Name");
            System.out.println("5. Sort Patients by Name");
            System.out.println("6. Add Patient to Treatment Queue");
            System.out.println("7. Treat Next Patient");
            System.out.println("8. View Treatment Queue");
            System.out.println("9. Discharge Patient");
            System.out.println("10. Undo Last Discharge");
            System.out.println("11. View Doctors & Workload");
            System.out.println("0. Exit");
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: addPatient(); break;
                case 2: viewAll(); break;
                case 3: searchById(); break;
                case 4: searchByName(); break;
                case 5: sortByName(); break;
                case 6: addToQueue(); break;
                case 7: treatNext(); break;
                case 8: viewQueue(); break;
                case 9: dischargePatient(); break;
                case 10: undoDischarge(); break;
                case 11: viewDoctors(); break;
                case 0: System.out.println("Thank you! Exiting..."); break;
                default: System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }
}
