class Grade {
    public static void main(String[] args) {
        int marks = 95;

        if (marks >= 70) {
            System.out.println("Passed");
            if (marks > 90)
                System.out.println("Grade A");
        } else
            System.out.println("Failed");
    }
}
