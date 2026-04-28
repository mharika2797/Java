# Generics, Enums & Iterators

## Generics

Write code that works with any type while maintaining type safety at compile time.

```java
// Generic class
public class Box<T> {
    private T value;
    public void set(T value) { this.value = value; }
    public T get() { return value; }
}
Box<String> box = new Box<>();
box.set("hello");

// Generic method
public <T> void print(T item) { System.out.println(item); }

// Bounded type — T must be a Number or subclass
public <T extends Number> double sum(List<T> list) { ... }
```

**Wildcards:**
```java
List<?>            // unknown type — read-only
List<? extends Number>  // Number or subtype (covariant) — read
List<? super Integer>   // Integer or supertype (contravariant) — write
```

**Why use generics?**
- Type safety at compile time (no ClassCastException at runtime)
- Eliminates casting
- Enables reusable data structures

---

## Enums

A special class representing a fixed set of constants.

```java
public enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

Day d = Day.MON;
d.name()    // "MON"
d.ordinal() // 0 (position)
Day.valueOf("TUE") // Day.TUE
Day.values() // all constants as array

// Enum with fields
public enum Planet {
    EARTH(5.976e+24, 6.37814e6),
    MARS(6.421e+23, 3.3972e6);

    private final double mass;
    private final double radius;

    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }
}

// Enum in switch
switch (day) {
    case MON: System.out.println("Monday"); break;
    default:  System.out.println("Other");
}
```

**Key facts:**
- Enums are implicitly `public static final`.
- Enums can implement interfaces.
- Enums cannot extend other classes (they extend `Enum` implicitly).
- Enums are singletons by nature.

---

## Iterator

Provides a way to traverse a collection without exposing its internal structure.

```java
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.equals("remove me")) it.remove(); // safe removal during iteration
}
```

**Why use Iterator over for-each for removal?**
Removing from a collection during a for-each loop causes `ConcurrentModificationException`. `Iterator.remove()` is safe.

**Iterable interface:**
Any class implementing `Iterable<T>` can be used in a for-each loop.
```java
public class NumberRange implements Iterable<Integer> {
    public Iterator<Integer> iterator() { ... }
}
```

---

## Common Interview Questions

**Q: What is type erasure?**
Generics are a compile-time feature. At runtime, generic type info is erased — `List<String>` and `List<Integer>` are both just `List` at runtime.

**Q: Can you create a generic array?**
No: `new T[10]` is not allowed due to type erasure. Use `List<T>` or cast from `Object[]`.

**Q: What is the difference between `List<?>` and `List<Object>`?**
`List<Object>` accepts any object but a `List<String>` cannot be assigned to it. `List<?>` accepts any list of any type but you can't add to it (except null).

**Q: Why can't a static method use the class's generic type?**
Static members belong to the class, not instances. Since generics are tied to instances, a static method must declare its own generic type parameter.

**Q: Can an enum have abstract methods?**
Yes. Each constant must then provide an implementation:
```java
enum Op { PLUS { int apply(int a, int b) { return a + b; } }; abstract int apply(int a, int b); }
```
