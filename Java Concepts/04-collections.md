# Collections — List, Set, Map, Queue

## Collections Hierarchy (simplified)

```
Iterable
  └── Collection
        ├── List      → ArrayList, LinkedList, Vector, Stack
        ├── Set       → HashSet, LinkedHashSet, TreeSet
        └── Queue     → PriorityQueue, ArrayDeque

Map (separate hierarchy)
  └── HashMap, LinkedHashMap, TreeMap, Hashtable
```

---

## List

Ordered, allows duplicates, index-based access.

```java
List<String> list = new ArrayList<>();
list.add("a");
list.add(0, "b");        // insert at index
list.get(0);             // "b"
list.set(0, "c");        // update
list.remove(0);          // remove by index
list.remove("a");        // remove by value
list.size();
list.contains("a");
list.indexOf("a");
list.subList(0, 2);
Collections.sort(list);
Collections.reverse(list);
```

**ArrayList vs LinkedList:**
| | ArrayList | LinkedList |
|---|---|---|
| Backed by | Array | Doubly linked list |
| Random access | O(1) | O(n) |
| Insert/delete middle | O(n) | O(1) |
| Use when | Reads are frequent | Inserts/deletes are frequent |

---

## Set

No duplicates, no guaranteed order (depends on implementation).

```java
Set<Integer> set = new HashSet<>();    // no order
Set<Integer> set = new LinkedHashSet<>(); // insertion order
Set<Integer> set = new TreeSet<>();    // sorted order

set.add(1);
set.remove(1);
set.contains(1);
set.size();
```

| | HashSet | LinkedHashSet | TreeSet |
|---|---|---|---|
| Order | None | Insertion order | Sorted |
| Null | 1 allowed | 1 allowed | Not allowed |
| Performance | O(1) | O(1) | O(log n) |

---

## HashMap

Key-value pairs. No order. One null key allowed.

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.get("a");            // 1
map.getOrDefault("b", 0); // 0 if not found
map.containsKey("a");
map.containsValue(1);
map.remove("a");
map.size();
map.putIfAbsent("b", 2);

// Iterate
for (Map.Entry<String, Integer> e : map.entrySet()) {
    System.out.println(e.getKey() + " = " + e.getValue());
}
map.forEach((k, v) -> System.out.println(k + " = " + v));
map.keySet();    // Set of keys
map.values();    // Collection of values
```

**HashMap vs LinkedHashMap vs TreeMap:**
| | HashMap | LinkedHashMap | TreeMap |
|---|---|---|---|
| Order | None | Insertion order | Sorted by key |
| Null key | Yes | Yes | No |
| Performance | O(1) | O(1) | O(log n) |

**HashMap vs Hashtable:**
| HashMap | Hashtable |
|---|---|
| Not synchronized | Synchronized |
| Allows null key/value | No nulls |
| Preferred | Legacy class |

---

## Queue / Deque

```java
Queue<Integer> q = new LinkedList<>();
q.offer(1);   // add (returns false if full)
q.poll();     // remove head (returns null if empty)
q.peek();     // view head (no remove)

Deque<Integer> dq = new ArrayDeque<>();
dq.offerFirst(1); // add to front
dq.offerLast(2);  // add to back
dq.pollFirst();
dq.pollLast();
```

**PriorityQueue** — min-heap by default:
```java
PriorityQueue<Integer> pq = new PriorityQueue<>();           // min
PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); // max
pq.offer(5); pq.offer(1); pq.offer(3);
pq.poll(); // returns 1 (smallest)
```

---

## Common Interview Questions

**Q: How does HashMap work internally?**
HashMap uses an array of buckets. The key's `hashCode()` determines the bucket. Within a bucket, entries are stored in a linked list (or tree if > 8 entries — Java 8+). `equals()` is used to find the exact key.

**Q: What happens if two keys have the same hashCode?**
Hash collision. Both are stored in the same bucket as a linked list. Retrieval uses `equals()` to find the right key.

**Q: What is the load factor in HashMap?**
Default 0.75. When 75% of capacity is filled, the map resizes (doubles). Balances space vs time.

**Q: HashSet vs HashMap?**
HashSet internally uses a HashMap where the element is the key and a dummy object is the value.

**Q: Fail-fast vs fail-safe iterators?**
- Fail-fast: throws `ConcurrentModificationException` if collection modified during iteration (ArrayList, HashMap).
- Fail-safe: works on a copy; no exception (`CopyOnWriteArrayList`, `ConcurrentHashMap`).

**Q: When to use TreeMap?**
When you need keys in sorted order. Uses Red-Black tree internally.
