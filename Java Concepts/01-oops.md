# OOP Concepts

## The 4 Pillars

### 1. Encapsulation
Wrapping data (fields) and methods into a class, and restricting direct access via access modifiers.
```java
public class Account {
    private double balance;  // hidden
    public double getBalance() { return balance; }  // controlled access
}
```
**Interview tip:** "Encapsulation is about data hiding. Use private fields + public getters/setters."

---

### 2. Inheritance
A child class acquires properties and behavior of a parent class using `extends`.
```java
class Animal { void eat() { System.out.println("eating"); } }
class Dog extends Animal { void bark() { System.out.println("barking"); } }
```
- Java supports **single inheritance** (one parent class).
- Multiple inheritance via **interfaces**.

---

### 3. Polymorphism
Same method name, different behavior.

**Compile-time (Method Overloading):** Same method name, different parameters.
```java
int add(int a, int b) { return a + b; }
double add(double a, double b) { return a + b; }
```

**Runtime (Method Overriding):** Child class redefines parent method.
```java
class Animal { void sound() { System.out.println("..."); } }
class Cat extends Animal { @Override void sound() { System.out.println("Meow"); } }
```

---

### 4. Abstraction
Hiding implementation details, exposing only what's necessary.

- **Abstract class:** Can have abstract + concrete methods. Cannot be instantiated.
- **Interface:** All methods are abstract by default (Java 8+ allows default/static methods).

```java
abstract class Shape { abstract double area(); }

interface Drawable { void draw(); }
```

---

## Abstract Class vs Interface

| | Abstract Class | Interface |
|---|---|---|
| Methods | Abstract + concrete | Abstract (default/static in Java 8+) |
| Variables | Any type | `public static final` only |
| Inheritance | `extends` (single) | `implements` (multiple) |
| Constructor | Yes | No |
| Use when | Shared base behavior | Defining a contract |

---

## Key Terms

| Term | One-liner |
|---|---|
| `super` | Refers to parent class constructor/method |
| `this` | Refers to current class instance |
| `final` class | Cannot be extended |
| `final` method | Cannot be overridden |
| `static` | Belongs to the class, not an instance |
| Coupling | How dependent classes are on each other (low is good) |
| Cohesion | How focused a class is on one job (high is good) |

---

## Common Interview Questions

**Q: Can we override a static method?**
No. Static methods are resolved at compile time (method hiding, not overriding).

**Q: Can we override a private method?**
No. Private methods are not visible to child classes.

**Q: What is constructor chaining?**
Calling one constructor from another using `this()` (same class) or `super()` (parent class).

**Q: Difference between overloading and overriding?**
- Overloading = compile-time, same class, different signature.
- Overriding = runtime, parent-child, same signature.

**Q: What is an IS-A vs HAS-A relationship?**
- IS-A → Inheritance (`Dog extends Animal`)
- HAS-A → Composition (`Car has an Engine`)
