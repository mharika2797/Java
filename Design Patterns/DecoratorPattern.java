package com.company;

public class DecoratorPattern {
    public static void main(String[] args) {
        Coffee plain = new SimpleCoffee();
        System.out.println(plain.getDescription() + "  $" + plain.getCost());

        Coffee withMilk = new Milk(plain);
        System.out.println(withMilk.getDescription() + "  $" + withMilk.getCost());

        Coffee withMilkAndSugar = new Sugar(new Milk(plain));
        System.out.println(withMilkAndSugar.getDescription() + "  $" + withMilkAndSugar.getCost());

        // Decorators stack in any order
        Coffee fancy = new Vanilla(new Sugar(new Milk(new SimpleCoffee())));
        System.out.println(fancy.getDescription() + "  $" + fancy.getCost());
    }
}

// Component interface
sealed interface Coffee permits SimpleCoffee, CoffeeDecorator {
    String getDescription();
    double getCost();
}

// Concrete component — the base object being decorated
final class SimpleCoffee implements Coffee {
    @Override public String getDescription() { return "Coffee"; }
    @Override public double getCost()        { return 1.00; }
}

// Base decorator — wraps any Coffee and delegates to it
abstract sealed class CoffeeDecorator implements Coffee permits Milk, Sugar, Vanilla {
    protected final Coffee coffee;
    CoffeeDecorator(Coffee coffee) { this.coffee = coffee; }
}

// Concrete decorators
final class Milk extends CoffeeDecorator {
    Milk(Coffee coffee) { super(coffee); }
    @Override public String getDescription() { return coffee.getDescription() + ", Milk"; }
    @Override public double getCost()        { return coffee.getCost() + 0.25; }
}

final class Sugar extends CoffeeDecorator {
    Sugar(Coffee coffee) { super(coffee); }
    @Override public String getDescription() { return coffee.getDescription() + ", Sugar"; }
    @Override public double getCost()        { return coffee.getCost() + 0.10; }
}

final class Vanilla extends CoffeeDecorator {
    Vanilla(Coffee coffee) { super(coffee); }
    @Override public String getDescription() { return coffee.getDescription() + ", Vanilla"; }
    @Override public double getCost()        { return coffee.getCost() + 0.50; }
}

/*
Notes:
1) Decorator pattern attaches additional responsibilities to an object dynamically — a flexible alternative to subclassing
2) Decorators wrap the component and add behavior before/after delegating; they can be stacked in any order
3) sealed interface + sealed abstract class (Java 17+) makes the permitted type hierarchy explicit at compile time
4) Java's I/O streams are the classic real-world example: BufferedReader wrapping FileReader, GZIPOutputStream wrapping FileOutputStream
5) Follows Open/Closed Principle — add new decorators without modifying existing classes
6) Difference from Proxy: Decorator adds behavior, Proxy controls access
 */
