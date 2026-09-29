abstract class Shape {
    abstract void area();
}

class Circle extends Shape {
    void area() {
        System.out.println("Circle Area");
    }
}

class Square extends Shape {
    void area() {
        System.out.println("Square Area");
    }
}

class Main {
    public static void main(String[] args) {
        new Circle().area();
        new Square().area();
    }
}
