class Student {
    String name;
    int marks;

    Student(String n, int m) {
        name = n;
        marks = m;
    }

    void display() {
        System.out.println(name + " " + marks);
    }
}

class Main {
    public static void main(String[] args) {
        Student s1 = new Student("John", 85);
        Student s2 = new Student("Mary", 90);

        s1.display();
        s2.display();
    }
}
