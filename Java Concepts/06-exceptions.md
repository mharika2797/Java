# Exceptions

## Exception Hierarchy

```
Throwable
  ├── Error           (JVM-level, don't catch: OutOfMemoryError, StackOverflowError)
  └── Exception
        ├── Checked Exception    (must handle: IOException, SQLException)
        └── RuntimeException     (unchecked: NullPointerException, IllegalArgumentException)
```

---

## Checked vs Unchecked

| | Checked | Unchecked |
|---|---|---|
| Compile-time check | Yes — must catch or declare | No |
| Extends | `Exception` | `RuntimeException` |
| Examples | `IOException`, `SQLException` | `NullPointerException`, `ArrayIndexOutOfBoundsException` |
| Use when | External failures (file, DB, network) | Programming errors |

---

## try-catch-finally

```java
try {
    // risky code
} catch (IOException e) {
    // handle IO error
} catch (Exception e) {
    // catch-all (always last)
} finally {
    // always runs — use for cleanup (close resources)
}
```

**Multi-catch (Java 7+):**
```java
catch (IOException | SQLException e) { ... }
```

---

## try-with-resources (Java 7+)

Automatically closes resources that implement `AutoCloseable`. No need for `finally`.

```java
try (FileReader fr = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(fr)) {
    return br.readLine();
} catch (IOException e) {
    e.printStackTrace();
}
// fr and br are closed automatically
```

---

## throw vs throws

```java
// throw — actually throws an exception
throw new IllegalArgumentException("Invalid input");

// throws — declares that a method may throw a checked exception
public void readFile(String path) throws IOException { ... }
```

---

## Custom Exception

```java
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// Usage
throw new InsufficientFundsException("Balance too low");
```

Extend `RuntimeException` for unchecked, `Exception` for checked custom exceptions.

---

## Exception Methods

```java
e.getMessage()     // error message
e.getCause()       // root cause
e.printStackTrace() // full stack trace to stderr
e.getClass().getName() // exception class name
```

---

## Common Interview Questions

**Q: What is the difference between `throw` and `throws`?**
- `throw` — used inside method body to actually throw an exception.
- `throws` — used in method signature to declare potential checked exceptions.

**Q: Can `finally` block be skipped?**
Only if `System.exit()` is called or the JVM crashes.

**Q: What happens if an exception is thrown in `finally`?**
It overrides the original exception. The original is lost.

**Q: Can we catch `Error`?**
Technically yes, but you shouldn't. Errors (like `OutOfMemoryError`) are JVM-level problems you can't recover from.

**Q: What is exception chaining?**
Wrapping one exception inside another to preserve the original cause.
```java
catch (IOException e) {
    throw new RuntimeException("Failed to read file", e);
}
// Access original: e.getCause()
```

**Q: Difference between `final`, `finally`, `finalize`?**
- `final` — keyword for constants, non-overridable methods, non-extendable classes.
- `finally` — block that always executes after try-catch.
- `finalize()` — deprecated method called by GC before object destruction.

**Q: What is a StackOverflowError?**
Thrown when the call stack exceeds its limit — typically caused by infinite recursion.
