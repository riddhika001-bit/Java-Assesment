public class ExceptionHandlingDemo {
    public static void main(String[] args) {
        try {
            // 1. This line will trigger an ArithmeticException (Division by zero)
            int result = 10 / 0; 
            System.out.println("Result: " + result);

            // 2. This line would trigger an ArrayIndexOutOfBoundsException,
            // but the try block stops as soon as the first exception happens.
            int[] numbers = {1, 2, 3};
            System.out.println("Array Element: " + numbers[5]);

        } catch (ArithmeticException e) {
            // Handles mathematical errors like dividing by zero
            System.out.println("Catch Block 1: Cannot divide by zero! (" + e + ")");

        } catch (ArrayIndexOutOfBoundsException e) {
            // Handles accessing a list index that does not exist
            System.out.println("Catch Block 2: The array index is out of bounds! (" + e + ")");

        } finally {
            // This block always runs, no matter what happens above
            System.out.println("Finally Block: Cleanup operations or final messages go here.");
        }

        System.out.println("Execution continues smoothly...");
    }
}
