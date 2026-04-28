# Strings, StringBuilder, StringBuffer

## String

- **Immutable** — once created, value cannot change.
- Stored in the **String Pool** (heap). Literals are reused.

```java
String s1 = "hello";        // goes to String pool
String s2 = new String("hello");  // new object on heap
s1 == s2      // false (different references)
s1.equals(s2) // true  (same content) ← always use .equals()
```

---

## Common String Methods

```java
String s = "Hello World";

s.length()           // 11
s.charAt(0)          // 'H'
s.indexOf("World")   // 6
s.substring(6)       // "World"
s.substring(0, 5)    // "Hello"
s.toLowerCase()      // "hello world"
s.toUpperCase()      // "HELLO WORLD"
s.trim()             // removes leading/trailing whitespace
s.replace("World", "Java") // "Hello Java"
s.contains("Hello")  // true
s.startsWith("He")   // true
s.endsWith("ld")     // true
s.split(" ")         // ["Hello", "World"]
s.isEmpty()          // false
s.isBlank()          // false (Java 11+, checks whitespace too)
s.strip()            // like trim() but Unicode-aware (Java 11+)
String.valueOf(42)   // "42"
"hello".toCharArray() // ['h','e','l','l','o']
```

---

## StringBuilder

- **Mutable** — modifies in place. No new object on each change.
- **Not thread-safe** — use in single-threaded contexts.
- Preferred for string manipulation in loops.

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" World");
sb.insert(5, ",");    // "Hello, World"
sb.delete(5, 6);      // removes ","
sb.reverse();         // "dlroW olleH"
sb.toString();        // convert back to String
sb.length();
sb.charAt(0);
sb.replace(0, 5, "Hi"); // replaces range
```

---

## StringBuffer

- Same as StringBuilder but **thread-safe** (synchronized).
- Slower than StringBuilder due to synchronization overhead.
- Use when multiple threads access the same buffer.

---

## String vs StringBuilder vs StringBuffer

| | String | StringBuilder | StringBuffer |
|---|---|---|---|
| Mutable | No | Yes | Yes |
| Thread-safe | Yes (immutable) | No | Yes |
| Performance | Slow (in loops) | Fast | Moderate |
| Use when | Fixed values | Single thread | Multi-thread |

---

## String Pool

```java
String a = "java";
String b = "java";
a == b // true — same pool reference

String c = new String("java");
a == c // false — c is on heap, not pool
a.equals(c) // true
c.intern() == a // true — intern() puts c into pool
```

---

## Common Interview Questions

**Q: Why is String immutable in Java?**
Security (no one can alter a DB URL mid-flight), thread-safety (safe to share), and String pool efficiency (reuse same literal).

**Q: `==` vs `.equals()` for Strings?**
`==` compares references. `.equals()` compares content. Always use `.equals()`.

**Q: How to reverse a String?**
```java
new StringBuilder(str).reverse().toString();
```

**Q: How to check if a String is a palindrome?**
```java
str.equals(new StringBuilder(str).reverse().toString());
```

**Q: What is String interning?**
`String.intern()` puts a string into the pool and returns the pool reference. Allows `==` comparison.

**Q: Why use StringBuilder in loops?**
`String + String` creates a new object each iteration → O(n²) memory. StringBuilder appends in place → O(n).
