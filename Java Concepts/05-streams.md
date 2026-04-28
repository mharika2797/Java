# Java Streams (Java 8+)

## What is a Stream?

A sequence of elements that supports functional-style operations. Streams do **not store data** — they process data from a source (collection, array, I/O).

**Pipeline = Source → Intermediate ops → Terminal op**

```java
list.stream()
    .filter(x -> x > 5)      // intermediate
    .map(x -> x * 2)          // intermediate
    .collect(Collectors.toList()); // terminal
```

---

## Creating Streams

```java
Stream.of(1, 2, 3);
Arrays.stream(arr);
list.stream();
list.parallelStream();           // multi-threaded
Stream.iterate(0, n -> n + 1);  // infinite stream
Stream.generate(Math::random);  // infinite stream
IntStream.range(0, 5);          // 0,1,2,3,4
IntStream.rangeClosed(1, 5);    // 1,2,3,4,5
```

---

## Intermediate Operations (lazy — return Stream)

```java
.filter(x -> x % 2 == 0)        // keep matching elements
.map(x -> x * 2)                 // transform each element
.mapToInt(String::length)        // map to IntStream
.flatMap(List::stream)           // flatten nested collections
.distinct()                      // remove duplicates
.sorted()                        // natural order
.sorted(Comparator.reverseOrder())
.limit(5)                        // take first 5
.skip(2)                         // skip first 2
.peek(x -> System.out.println(x)) // debug; passes element through
```

---

## Terminal Operations (eager — trigger processing)

```java
.collect(Collectors.toList())
.collect(Collectors.toSet())
.collect(Collectors.toMap(k, v))
.collect(Collectors.joining(", "))       // concat strings
.collect(Collectors.groupingBy(fn))      // group by key
.collect(Collectors.counting())
.collect(Collectors.partitioningBy(fn))  // true/false groups

.forEach(System.out::println)
.count()
.findFirst()          // Optional<T>
.findAny()            // Optional<T>
.anyMatch(x -> x > 5) // boolean
.allMatch(x -> x > 0)
.noneMatch(x -> x < 0)
.min(Comparator.naturalOrder()) // Optional<T>
.max(Comparator.naturalOrder())
.toArray()
.reduce(0, Integer::sum)        // fold to single value
```

---

## Common Stream Patterns

```java
List<String> names = List.of("Alice", "Bob", "Charlie");

// Filter + collect
names.stream().filter(n -> n.length() > 3).collect(Collectors.toList());

// Map to uppercase
names.stream().map(String::toUpperCase).collect(Collectors.toList());

// Sum of integers
List<Integer> nums = List.of(1,2,3,4,5);
int sum = nums.stream().mapToInt(Integer::intValue).sum();

// Group by length
Map<Integer, List<String>> grouped =
    names.stream().collect(Collectors.groupingBy(String::length));

// FlatMap — flatten list of lists
List<List<Integer>> nested = List.of(List.of(1,2), List.of(3,4));
List<Integer> flat = nested.stream().flatMap(Collection::stream).collect(Collectors.toList());

// Find first element matching condition
Optional<String> first = names.stream().filter(n -> n.startsWith("A")).findFirst();
first.ifPresent(System.out::println);
```

---

## Optional

Avoids `NullPointerException`. A container that may or may not hold a value.

```java
Optional<String> opt = Optional.of("hello");
Optional<String> empty = Optional.empty();
Optional<String> nullable = Optional.ofNullable(null);

opt.isPresent()         // true
opt.get()               // "hello" (throws if empty)
opt.orElse("default")   // "default" if empty
opt.orElseGet(() -> computeDefault())
opt.orElseThrow(() -> new RuntimeException())
opt.ifPresent(System.out::println)
opt.map(String::toUpperCase)  // returns Optional<String>
opt.filter(s -> s.length() > 3)
```

---

## Common Interview Questions

**Q: What is the difference between `map` and `flatMap`?**
- `map` transforms each element 1-to-1.
- `flatMap` transforms each element to a stream and merges them (flattens).

```java
// map: Stream<String[]>   flatMap: Stream<String>
stream.map(s -> s.split(""))
stream.flatMap(s -> Arrays.stream(s.split("")))
```

**Q: Are streams lazy?**
Yes. Intermediate operations are not executed until a terminal operation is called.

**Q: `stream()` vs `parallelStream()`?**
`parallelStream()` splits work across multiple CPU threads using the ForkJoinPool. Use only when data is large and operations are stateless and independent.

**Q: Can a Stream be reused?**
No. Once a terminal operation is called, the stream is consumed. Create a new stream to reuse.

**Q: What is `reduce()`?**
Combines all elements into one result.
```java
int sum = List.of(1,2,3).stream().reduce(0, Integer::sum); // 6
```

**Q: Difference between `findFirst()` and `findAny()`?**
`findFirst()` — deterministic, returns first element. `findAny()` — non-deterministic, faster in parallel streams.
