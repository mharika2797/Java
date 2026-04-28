public class LiskovSubstitution {
    public static void main(String[] args) {
        System.out.println("--- Wrong implementation ---");
        Bird b = new Bird();
        b.fly();
        Bird p = new BadPigeon();
        p.fly();
        Bird o = new BadOstrich();
        o.fly();

        System.out.println("--- Correct implementation ---");
        Pigeon pigeon = new Pigeon();
        pigeon.fly();
        Ostrich ostrich = new Ostrich();
        ostrich.walk();
    }
}

// Here BadPigeon and BadOstrich both extend Bird, since both fall under the bird category.
// But pigeon can fly while ostrich cannot — forcing Ostrich to inherit fly() violates LSP.
// So this does not abide by the Liskov Substitution Principle.

class Bird {
    public void fly() {
        System.out.println("fly fly");
    }
}

class BadPigeon extends Bird {
}

class BadOstrich extends Bird {
}

// The correct implementation by abiding with the Liskov Substitution Principle is as follows.
// Split the hierarchy so that fly() only lives where it belongs.

class BirdBase {
}

class FlyingBird extends BirdBase {
    public void fly() {
        System.out.println("fly fly");
    }
}

class NonFlyingBird extends BirdBase {
    public void walk() {
        System.out.println("walks on ground");
    }
}

class Pigeon extends FlyingBird {
}

class Ostrich extends NonFlyingBird {
}


/*
Notes
1_ A parent class must be substitutable by any of its subclasses without breaking the program.
2_ Any substitution of the subclass must not change the expected behavior of the parent class.
3_ Fix: split the hierarchy so subclasses only inherit what they actually support.
4_ Examples: bird (flying / non-flying), employees (technical / non-technical skills)
 */
