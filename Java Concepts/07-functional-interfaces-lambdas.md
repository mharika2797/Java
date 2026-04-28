# Functional Interfaces & Lambdas (Java 8+)

## Lambda Expression

A concise way to represent an anonymous function (implementation of a functional interface).

```java
// Before Java 8
Runnable r = new Runnable() {
    public void run() { System.out.println("hello"); }
};

// Lambda
Runnable r = () -> System.out.println("hello");
```

**Syntax:**
```
(parameters) -> expression
(parameters) -> { statements; }
```

---

## Functional Interface

An interface with **exactly one abstract method**. Annotated with `@FunctionalInterface`.

---

## Built-in Functional Interfaces

| Interface | Method | Input → Output | Use |
|---|---|---|---|
| `Predicate<T>` | `test(T)` | T → boolean | Filtering |
| `Function<T,R>` | `apply(T)` | T → R | Transforming |
| `Consumer<T>` | `accept(T)` | T → void | Consuming/printing |
| `Supplier<T>` | `get()` | () → T | Providing values |
| `BiFunction<T,U,R>` | `apply(T,U)` | T,U → R | Two inputs |
| `UnaryOperator<T>` | `apply(T)` | T → T | Same type in/out |
| `BinaryOperator<T>` | `apply(T,T)` | T,T → T | Combine two of same type |

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
isEven.test(4); // true

Function<String, Integer> strLen = String::length;
strLen.apply("hello"); // 5

Consumer<String> print = System.out::println;
print.accept("hi");

Supplier<List<String>> listSupplier = ArrayList::new;
```

---

## Method References

Shorthand for lambdas that call an existing method.

```java
// Static method
Function<String, Integer> parse = Integer::parseInt;

// Instance method of a particular object
Consumer<String> print = System.out::println;

// Instance method of an arbitrary object of a type
Function<String, String> upper = String::toUpperCase;

// Constructor reference
Supplier<ArrayList> newList = ArrayList::new;
```

---

## Comparator with Lambdas

```java
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");

names.sort((a, b) -> a.compareTo(b));               // ascending
names.sort(Comparator.naturalOrder());               // same
names.sort(Comparator.reverseOrder());               // descending
names.sort(Comparator.comparing(String::length));    // by length
names.sort(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
```

---

## Common Interview Questions

**Q: What is a functional interface?**
An interface with exactly one abstract method. Can have multiple default/static methods.

**Q: Can a lambda capture variables from the enclosing scope?**
Yes, but only **effectively final** variables (variables not modified after assignment).

```java
int x = 5; // effectively final
Runnable r = () -> System.out.println(x); // ok
x = 10;    // compile error — x is no longer effectively final
```

**Q: `Predicate.and()`, `.or()`, `.negate()`?**
```java
Predicate<Integer> gt5 = n -> n > 5;
Predicate<Integer> lt10 = n -> n < 10;
Predicate<Integer> between = gt5.and(lt10); // 6,7,8,9
```

**Q: What is the difference between `Function` and `Consumer`?**
`Function<T,R>` returns a result. `Consumer<T>` returns void — used for side effects.

**Q: What is `UnaryOperator`?**
A specialized `Function<T,T>` where input and output are the same type. E.g., `x -> x * 2`.
