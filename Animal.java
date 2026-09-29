class Animal {
    void eat() {
        System.out.println("Animal eats");
    }
}

class Dog extends Animal {
    void bark() {
        System.out.println("Dog barks");
    }
}

class Fox extends Animal {
    void sound() {
        System.out.println("Fox sounds");
    }
}

class Rabbit extends Animal {
    void jump() {
        System.out.println("Rabbit jumps");
    }
}

class Main {
    public static void main(String[] args) {
        new Dog().bark();
        new Fox().sound();
        new Rabbit().jump();
    }
}
