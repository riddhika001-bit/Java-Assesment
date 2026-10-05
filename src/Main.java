/*
 * Main.java
 * Starts the program: creates database + tables, then shows the menu.
 */
public class Main {

    public static void main(String[] args) {

        // 1. set up database and tables
        if (!CreateDatabase.createDatabase()) return;
        if (!CreateTable.createTables()) return;
        AllOperation.setupDoctors();

        // 2. menu
        int choice;
        do {
            System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
            System.out.println("1.  Add Patient");
            System.out.println("2.  View All Patients");
            System.out.println("3.  Search Patient by ID");
            System.out.println("4.  Search Patient by Name");
            System.out.println("5.  Update Patient");
            System.out.println("6.  Sort Patients by Name");
            System.out.println("7.  Add Patient to Treatment Queue");
            System.out.println("8.  Treat Next Patient");
            System.out.println("9.  View Treatment Queue");
            System.out.println("10. Discharge Patient");
            System.out.println("11. Undo Last Discharge");
            System.out.println("12. View Doctors & Workload");
            System.out.println("0.  Exit");
            choice = AllOperation.readInt("Enter your choice: ");

            switch (choice) {
                case 1:  AllOperation.addPatient(); break;
                case 2:  AllOperation.viewAll(); break;
                case 3:  AllOperation.searchById(); break;
                case 4:  AllOperation.searchByName(); break;
                case 5:  AllOperation.updatePatient(); break;
                case 6:  AllOperation.sortByName(); break;
                case 7:  AllOperation.addToQueue(); break;
                case 8:  AllOperation.treatNext(); break;
                case 9:  AllOperation.viewQueue(); break;
                case 10: AllOperation.dischargePatient(); break;
                case 11: AllOperation.undoDischarge(); break;
                case 12: AllOperation.viewDoctors(); break;
                case 0:  System.out.println("Thank you! Exiting..."); break;
                default: System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }
}
