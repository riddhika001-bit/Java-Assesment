import java.util.ArrayList;

// Changed class name to Main for online compiler compatibility
public class Main {
    public static void main(String[] args) {
        // Create an ArrayList to store tasks
        ArrayList<String> tasks = new ArrayList<>();

        // 1. ADDING tasks using add()
        tasks.add("Buy groceries");
        tasks.add("Pay electricity bill");
        tasks.add("Walk the dog");

        // 2. ITERATING over tasks
        System.out.println("Initial To-Do List:");
        printTasks(tasks);

        // 3. REMOVING a task by index (Removes "Pay electricity bill")
        tasks.remove(1);
        System.out.println("\nAfter removing index 1:");
        printTasks(tasks);
        
        // Removing a task by exact object value
        tasks.remove("Buy groceries");
        System.out.println("\nAfter removing 'Buy groceries':");
        printTasks(tasks);
    }

    // Helper method to iterate and display tasks
    public static void printTasks(ArrayList<String> taskList) {
        if (taskList.isEmpty()) {
            System.out.println("The to-do list is empty.");
        } else {
            for (int i = 0; i < taskList.size(); i++) {
                System.out.println((i + 1) + ". " + taskList.get(i));
            }
        }
    }
}
