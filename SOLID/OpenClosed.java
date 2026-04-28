public class OpenClosed {
    public static void main(String[] args) {
        // Using the base class type for both — polymorphism at work.
        // The caller does not need to know which subclass it has.
        Student phani = new Student(19, "Phani");
        phani.display();

        Student teja = new ChildStudent(20, "Teja");
        teja.display();
    }
}

// Base class: closed for modification.
// Once written and in use, we should NOT change this class —
// doing so risks breaking all existing callers who depend on its behavior.
class Student {
    protected int id;
    protected String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void display() {
        System.out.println("ID:   " + id);
        System.out.println("Name: " + name);
    }
}

// Extended class: open for extension.
// We get new behavior by extending Student, not by editing it.
// If this new behavior is wrong, we change only ChildStudent — Student is untouched.
class ChildStudent extends Student {
    ChildStudent(int id, String name) {
        // super() sets the parent fields cleanly — avoid assigning super.field directly.
        super(id, name);
    }

    @Override
    public void display() {
        super.display();                        // reuse parent behavior
        System.out.println("Hello, World!");    // add new behavior on top
    }
}

/*
Notes:
1) The Open/Closed Principle: a class should be open for extension
   but closed for modification.

2) "Closed for modification" means: once a class is tested and in use,
   don't change its existing methods. Other code depends on that behavior.
   Changing it can silently break callers you forgot about.

3) "Open for extension" means: add new behavior by creating a subclass
   and overriding methods. The original class stays untouched.

4) Use super(id, name) in the child constructor to delegate field
   initialization to the parent. Assigning super.id = id directly works
   but bypasses the parent constructor and is bad practice.

5) @Override tells the compiler you intend to override a parent method.
   If you misspell the method name, the compiler catches it immediately.
*/
