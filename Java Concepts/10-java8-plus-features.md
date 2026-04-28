# Java 8+ Key Features

## Java 8

| Feature | What it gives you |
|---|---|
| Lambda expressions | Anonymous functions |
| Functional interfaces | `Predicate`, `Function`, `Consumer`, `Supplier` |
| Stream API | Declarative data processing |
| Optional | Null-safe container |
| Default methods in interfaces | Add methods to interfaces without breaking implementations |
| Method references | `Class::method` shorthand for lambdas |
| `Date/Time API` (java.time) | Immutable, thread-safe date/time |

### Default & Static methods in interfaces
```java
interface Greeter {
    default void greet() { System.out.println("Hello!"); }
    static Greeter english() { return () -> System.out.println("Hi!"); }
}
```

### New Date/Time API
```java
LocalDate date = LocalDate.now();           // 2025-04-28
LocalTime time = LocalTime.now();           // 14:30:00
LocalDateTime dt = LocalDateTime.now();
ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("US/Eastern"));

date.plusDays(5);
date.isBefore(LocalDate.of(2025, 12, 31));
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
date.format(fmt);
```

---

## Java 9

- **Modules (Project Jigsaw):** `module-info.java` — modular JARs.
- **Collection factory methods:**
```java
List.of(1, 2, 3)       // immutable list
Set.of("a", "b")
Map.of("k1", 1, "k2", 2)
```
- **`Stream.takeWhile()` / `dropWhile()`**
- **`Optional.ifPresentOrElse()`**

---

## Java 10

- **`var` keyword** (local variable type inference):
```java
var list = new ArrayList<String>(); // inferred as ArrayList<String>
var name = "Alice";                  // inferred as String
```
Only for local variables — not fields, method params, or return types.

---

## Java 11

- **`String` new methods:** `isBlank()`, `strip()`, `stripLeading()`, `stripTrailing()`, `lines()`, `repeat(n)`
- **`var` in lambda parameters**
- **`HttpClient` (standard):** Replaces Apache HttpClient for HTTP calls.

---

## Java 14 / 15 / 16

- **Records (Java 16, stable):** Immutable data classes with less boilerplate.

### Record vs Class — Side by Side

**The same `Person` data object written both ways:**

```java
// ── REGULAR CLASS ──────────────────────────────────────────
public class PersonClass {
    private final String name;
    private final int age;

    // Constructor
    public PersonClass(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters
    public String getName() { return name; }
    public int getAge()     { return age; }

    // Must write manually
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonClass p)) return false;
        return age == p.age && name.equals(p.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name, age); }

    @Override
    public String toString() { return "PersonClass[name=" + name + ", age=" + age + "]"; }
}

// ── RECORD ─────────────────────────────────────────────────
public record PersonRecord(String name, int age) {}
// That's it. Constructor, getters, equals, hashCode, toString — all auto-generated.
```

**Usage — identical for both:**
```java
PersonClass pc = new PersonClass("Alice", 30);
pc.getName();   // "Alice"
pc.getAge();    // 30

PersonRecord pr = new PersonRecord("Alice", 30);
pr.name();      // "Alice"  ← accessor, not getName()
pr.age();       // 30

// equals works out of the box for Record
new PersonRecord("Alice", 30).equals(new PersonRecord("Alice", 30)); // true
// For class: only if you wrote equals() yourself

System.out.println(pr); // PersonRecord[name=Alice, age=30]
```

**Records can still have:**
```java
public record PersonRecord(String name, int age) {

    // Compact constructor — for validation
    public PersonRecord {
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
        name = name.trim(); // can transform before assignment
    }

    // Custom instance method
    public String greeting() {
        return "Hi, I'm " + name;
    }

    // Static factory method
    public static PersonRecord unknown() {
        return new PersonRecord("Unknown", 0);
    }
}
```

**What Records CANNOT do:**
```java
// Cannot extend another class (implicitly extends Record)
public record Bad(int x) extends SomeClass {} // ❌ compile error

// Cannot have mutable fields
public record Bad(int x) { int y; } // ❌ instance fields not allowed

// Cannot be abstract
abstract record Bad(int x) {} // ❌
```

### Record vs Class — Quick Comparison

| | Class | Record |
|---|---|---|
| Boilerplate | Constructor, getters, equals, hashCode, toString — all manual | All auto-generated |
| Mutability | Mutable by default | Immutable (fields are `final`) |
| Inheritance | Can extend/be extended | Cannot extend classes; can implement interfaces |
| Fields | Instance + static | Component fields (final) + static |
| Use when | Mutable objects, rich domain models | DTOs, value objects, data carriers |

**Rule of thumb:** Use `record` when the class is just a bag of data (DTO, response, config). Use a `class` when you need mutability or inheritance.

- **Pattern Matching for instanceof (Java 16):**
```java
// Before
if (obj instanceof String) { String s = (String) obj; ... }

// After
if (obj instanceof String s) { System.out.println(s.toUpperCase()); }
```

---

## Java 17 (LTS)

- **Sealed classes:** Restrict which classes can extend/implement.
```java
public sealed class Shape permits Circle, Rectangle, Triangle {}
public final class Circle extends Shape {}
```

- **Switch expressions (stable since 14):**
```java
int result = switch (day) {
    case MON, TUE -> 1;
    case WED -> 2;
    default -> 0;
};
```

---

## Java 21 (LTS)

- **Virtual threads (Project Loom):** Lightweight threads — millions can run concurrently.
```java
Thread.ofVirtual().start(() -> System.out.println("virtual thread"));
```

- **Pattern matching in switch:**
```java
switch (obj) {
    case Integer i -> System.out.println("int: " + i);
    case String s  -> System.out.println("str: " + s);
    default        -> System.out.println("other");
}
```

- **Sequenced Collections:** `getFirst()`, `getLast()` on List.

---

## Common Interview Questions

**Q: What are the main features of Java 8?**
Lambdas, Streams, Optional, Functional interfaces, Default interface methods, new Date/Time API.

**Q: What is `var` in Java 10?**
Local variable type inference — the compiler infers the type. Reduces verbosity. Cannot be used for fields or method parameters.

**Q: What is a Record in Java 16?**
A concise immutable data class. Auto-generates constructor, getters, `equals`, `hashCode`, and `toString`.

**Q: What are Virtual Threads (Java 21)?**
Lightweight threads managed by the JVM (not OS). Allow millions of concurrent threads without the overhead of platform threads. Ideal for I/O-bound tasks.

**Q: What is a Sealed class?**
A class that explicitly controls which classes can extend it using `permits`. Useful for exhaustive `switch` pattern matching.
