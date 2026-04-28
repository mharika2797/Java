import java.util.*;
import java.util.stream.*;

/**
 * JAVA SYNTAX CHEATSHEET
 * ──────────────────────
 * Everything you need to write Java programs from scratch.
 * Read this once and you'll know how to initialize, access,
 * loop, and manipulate every common data structure.
 *
 * Topics:
 *  1.  Primitives & Variables
 *  2.  Strings
 *  3.  Arrays
 *  4.  ArrayList
 *  5.  LinkedList
 *  6.  HashMap
 *  7.  HashSet
 *  8.  Stack & Queue & Deque
 *  9.  Loops (for, while, for-each, stream)
 * 10.  Conditionals & Switch
 * 11.  Methods
 * 12.  StringBuilder
 * 13.  Sorting & Comparators
 * 14.  Math utilities
 * 15.  Type conversions
 * 16.  Null safety & Optional
 */
class Java_Syntax_Cheatsheet {

    public static void main(String[] args) {

        // ════════════════════════════════════════════════════
        // 1. PRIMITIVES & VARIABLES
        // ════════════════════════════════════════════════════
        int     a    = 10;
        long    b    = 100_000_000L;    // L suffix required for long literals
        double  d    = 3.14;
        float   f    = 3.14f;           // f suffix required for float literals
        boolean flag = true;
        char    ch   = 'A';

        int  MAX  = Integer.MAX_VALUE;  // 2147483647
        int  MIN  = Integer.MIN_VALUE;  // -2147483648
        long LMAX = Long.MAX_VALUE;     // 9223372036854775807

        var x    = 42;        // type inferred as int  (Java 10+)
        var word = "Alice";   // type inferred as String


        // ════════════════════════════════════════════════════
        // 2. STRINGS  — immutable, stored in String Pool
        // ════════════════════════════════════════════════════
        String s      = "Hello World";
        String empty  = "";
        String fromNum  = String.valueOf(42);       // int  → "42"
        String fromChar = String.valueOf('A');      // char → "A"

        // --- Access ---
        int    len  = s.length();                  // 11  ← method, has ()
        char   c0   = s.charAt(0);                 // 'H'
        int    idx  = s.indexOf("World");          // 6  (-1 if not found)
        String sub1 = s.substring(6);             // "World"
        String sub2 = s.substring(0, 5);          // "Hello"  (end exclusive)

        // --- Check ---
        boolean has   = s.contains("Hello");       // true
        boolean start = s.startsWith("He");        // true
        boolean end2  = s.endsWith("ld");          // true
        boolean blank = "  ".isBlank();            // true (Java 11+)
        boolean emp   = "".isEmpty();              // true

        // --- Transform ---
        String upper  = s.toUpperCase();           // "HELLO WORLD"
        String lower  = s.toLowerCase();           // "hello world"
        String strip  = "  hi  ".strip();          // "hi"  (prefer over trim())
        String repl   = s.replace("World","Java"); // "Hello Java"
        String[] parts = s.split(" ");             // ["Hello", "World"]
        String joined = String.join("-","a","b");  // "a-b"
        char[]  chars = s.toCharArray();           // ['H','e','l','l','o',...]
        String  rep   = "ha".repeat(3);            // "hahaha" (Java 11+)

        // --- Compare — ALWAYS .equals(), NEVER == ---
        String s1 = "hello", s2 = "hello";
        boolean eq  = s1.equals(s2);                    // true
        boolean eqi = s1.equalsIgnoreCase("HELLO");     // true
        int     cmp = s1.compareTo(s2);                 // 0 (neg/pos if different)


        // ════════════════════════════════════════════════════
        // 3. ARRAYS  — fixed size, zero-indexed
        // ════════════════════════════════════════════════════
        int[]   arr1 = new int[5];              // [0, 0, 0, 0, 0]
        int[]   arr2 = {1, 2, 3, 4, 5};        // initialized
        int[][] grid = {{1,2,3},{4,5,6}};       // 2D array

        // --- Access ---
        int first  = arr2[0];                   // 1
        int last   = arr2[arr2.length - 1];     // 5   ← .length is a FIELD (no parentheses)
        int rows   = grid.length;               // 2
        int cols   = grid[0].length;            // 3

        // --- Utilities (java.util.Arrays) ---
        Arrays.sort(arr2);                            // sort ascending, in-place
        Arrays.fill(arr1, 7);                         // [7,7,7,7,7]
        int[]   copy  = Arrays.copyOf(arr2, arr2.length);
        int[]   range = Arrays.copyOfRange(arr2, 1, 4); // index 1 to 3 inclusive
        boolean same  = Arrays.equals(arr1, arr2);
        System.out.println(Arrays.toString(arr2));       // print 1D nicely
        System.out.println(Arrays.deepToString(grid));   // print 2D nicely


        // ════════════════════════════════════════════════════
        // 4. ARRAYLIST  — dynamic size, index-based
        // ════════════════════════════════════════════════════
        List<Integer> list  = new ArrayList<>();
        List<String>  names = new ArrayList<>(Arrays.asList("Alice","Bob","Charlie"));
        List<Integer> fixed = List.of(1, 2, 3);        // immutable, Java 9+

        // --- Add ---
        list.add(10);                   // append to end
        list.add(0, 99);                // insert at index 0

        // --- Access ---
        int val  = list.get(0);         // get by index
        int sz   = list.size();         // number of elements  ← .size() not .length
        boolean has2 = list.contains(10);
        int pos  = list.indexOf(10);    // -1 if not found

        // --- Update / Remove ---
        list.set(0, 55);                        // update index 0
        list.remove(Integer.valueOf(10));        // remove by VALUE  ← must box int
        list.remove(0);                         // remove by INDEX
        list.clear();                           // empty the list

        // --- Sort ---
        Collections.sort(names);                               // alphabetical
        Collections.sort(names, Comparator.reverseOrder());    // reverse alpha
        names.sort(Comparator.comparingInt(String::length));   // by string length

        // --- Convert to array ---
        String[] arr = names.toArray(new String[0]);


        // ════════════════════════════════════════════════════
        // 5. LINKEDLIST  — fast insert/delete at ends
        // ════════════════════════════════════════════════════
        LinkedList<Integer> ll = new LinkedList<>();

        // --- Add ---
        ll.add(1);          // append to end
        ll.addFirst(0);     // prepend  → [0, 1]
        ll.addLast(2);      // append   → [0, 1, 2]
        ll.add(1, 99);      // insert at index 1 → [0, 99, 1, 2]

        // --- Access ---
        int llFirst = ll.getFirst();    // 0  — peek front
        int llLast  = ll.getLast();     // 2  — peek back
        int llAt    = ll.get(1);        // 99 — by index (O(n) — slow)
        int llSize  = ll.size();

        // --- Remove ---
        ll.removeFirst();               // removes & returns front
        ll.removeLast();                // removes & returns back
        ll.remove(Integer.valueOf(99)); // removes first occurrence of value 99

        // --- Iterate ---
        for (int item : ll) System.out.print(item + " ");
        for (int i = 0; i < ll.size(); i++) System.out.print(ll.get(i) + " ");


        // ════════════════════════════════════════════════════
        // 6. HASHMAP  — key-value pairs, O(1) get/put
        // ════════════════════════════════════════════════════
        Map<String, Integer> map    = new HashMap<>();         // no order
        Map<String, Integer> lmap   = new LinkedHashMap<>();   // insertion order
        Map<String, Integer> tmap   = new TreeMap<>();         // sorted by key

        // --- Put / Update ---
        map.put("apple", 3);
        map.put("banana", 5);
        map.putIfAbsent("apple", 99);         // won't overwrite existing key
        map.merge("apple", 1, Integer::sum);  // apple: 3 → 4  (add 1 to existing)

        // --- Access ---
        int cnt  = map.get("apple");                  // 4
        int def  = map.getOrDefault("mango", 0);      // 0 (key not found)
        boolean hasK = map.containsKey("apple");      // true
        boolean hasV = map.containsValue(5);           // true
        int msz  = map.size();

        // --- Remove ---
        map.remove("banana");

        // --- Iterate (3 ways) ---
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
        for (String key : map.keySet())  System.out.println(key);
        for (int v   : map.values())     System.out.println(v);
        map.forEach((k, v) -> System.out.println(k + "=" + v));

        // --- Frequency count pattern (very common in interviews) ---
        int[] nums = {1, 2, 2, 3, 3, 3};
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);
        // freq = {1=1, 2=2, 3=3}
        // equivalent:  freq.put(n, freq.getOrDefault(n, 0) + 1);


        // ════════════════════════════════════════════════════
        // 7. HASHSET  — unique elements, O(1) lookup
        // ════════════════════════════════════════════════════
        Set<Integer> set  = new HashSet<>();          // no order
        Set<Integer> lset = new LinkedHashSet<>();    // insertion order
        Set<Integer> tset = new TreeSet<>();           // sorted

        set.add(1); set.add(2); set.add(2);  // {1, 2}  — duplicate ignored
        boolean in   = set.contains(1);      // true
        boolean added = set.add(1);          // false — already exists (use this for dup detection)
        set.remove(1);
        int ssz = set.size();                // 1

        // --- Seen-set pattern (detect duplicates) ---
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) {
            if (!seen.add(n)) System.out.println("Duplicate: " + n);
        }


        // ════════════════════════════════════════════════════
        // 8. STACK, QUEUE, DEQUE, PRIORITY QUEUE
        // ════════════════════════════════════════════════════

        // Stack — LIFO (use Deque, NOT the legacy Stack class)
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1);           // push to top
        stack.push(2);           // [2, 1]
        int top    = stack.peek(); // look at top = 2 (no remove)
        int popped = stack.pop();  // remove top = 2
        boolean se = stack.isEmpty();

        // Queue — FIFO
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(1);           // enqueue to back
        queue.offer(2);           // [1, 2]
        int front  = queue.peek(); // look at front = 1 (no remove)
        int polled = queue.poll(); // remove front = 1
        boolean qe = queue.isEmpty();

        // Deque — double-ended (works as both stack and queue)
        Deque<Integer> dq = new ArrayDeque<>();
        dq.offerFirst(1);  dq.offerLast(2);    // add to front / back
        dq.peekFirst();    dq.peekLast();       // look front / back
        dq.pollFirst();    dq.pollLast();       // remove front / back

        // PriorityQueue — min-heap by default
        PriorityQueue<Integer> minPQ = new PriorityQueue<>();
        PriorityQueue<Integer> maxPQ = new PriorityQueue<>(Collections.reverseOrder());
        minPQ.offer(5); minPQ.offer(1); minPQ.offer(3);
        minPQ.poll();   // removes & returns 1 (smallest)
        minPQ.peek();   // looks at 3 (next smallest, no remove)


        // ════════════════════════════════════════════════════
        // 9. LOOPS
        // ════════════════════════════════════════════════════

        // Classic index loop
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");  // 0 1 2 3 4
        }

        // Reverse loop
        for (int i = 4; i >= 0; i--) {
            System.out.print(i + " ");  // 4 3 2 1 0
        }

        // For-each on array
        int[] data = {10, 20, 30};
        for (int item : data) System.out.print(item + " ");

        // For-each on List
        for (String name : names) System.out.println(name);

        // For-each on Map
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            System.out.println(e.getKey() + " = " + e.getValue());
        }

        // For-each on 2D array
        for (int[] row : grid) {
            for (int cell : row) System.out.print(cell + " ");
            System.out.println();
        }

        // While loop
        int i = 0;
        while (i < 5) { System.out.print(i + " "); i++; }

        // Do-while — runs at least once
        int j = 0;
        do { System.out.print(j + " "); j++; } while (j < 3);

        // Loop with index on List
        for (int k = 0; k < names.size(); k++) {
            System.out.println(k + ": " + names.get(k));
        }

        // Stream forEach
        names.forEach(n -> System.out.println(n.toUpperCase()));

        // break and continue
        for (int n : data) {
            if (n == 20) continue;  // skip this iteration
            if (n == 30) break;     // exit loop entirely
            System.out.println(n);
        }


        // ════════════════════════════════════════════════════
        // 10. CONDITIONALS & SWITCH
        // ════════════════════════════════════════════════════

        int score = 85;
        if (score >= 90)       System.out.println("A");
        else if (score >= 80)  System.out.println("B");
        else                   System.out.println("C");

        // Ternary operator
        String grade = score >= 90 ? "A" : "B or below";

        // Switch statement (classic)
        int day = 3;
        switch (day) {
            case 1:  System.out.println("Mon"); break;
            case 2:  System.out.println("Tue"); break;
            default: System.out.println("Other");
        }

        // Switch expression — Java 14+ (no break needed)
        String dayName = switch (day) {
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            default -> "Other";
        };


        // ════════════════════════════════════════════════════
        // 12. STRINGBUILDER  — mutable string, use in loops
        // ════════════════════════════════════════════════════
        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append(" ").append("World");  // chaining
        sb.insert(5, ",");               // "Hello, World"
        sb.delete(5, 6);                 // removes ","  → "Hello World"
        sb.replace(0, 5, "Hi");          // "Hi World"
        sb.reverse();                    // "dlroW iH"
        int    sbLen = sb.length();
        char   sbCh  = sb.charAt(0);
        String sbStr = sb.toString();    // convert back to String

        // Common pattern: join items in a loop
        StringBuilder csv = new StringBuilder();
        int[] vals = {1, 2, 3, 4, 5};
        for (int n : vals) {
            if (csv.length() > 0) csv.append(",");
            csv.append(n);
        }
        System.out.println(csv.toString()); // "1,2,3,4,5"


        // ════════════════════════════════════════════════════
        // 13. SORTING & COMPARATORS
        // ════════════════════════════════════════════════════
        int[] intArr = {5, 2, 8, 1};
        Arrays.sort(intArr);  // [1,2,5,8] — works directly on primitives

        Integer[] boxed = {5, 2, 8, 1};
        Arrays.sort(boxed, Comparator.reverseOrder());        // [8,5,2,1]
        Arrays.sort(boxed, (p, q) -> q - p);                 // same, manual lambda

        List<String> ws = new ArrayList<>(Arrays.asList("banana","apple","fig"));
        ws.sort(Comparator.comparingInt(String::length));     // by length asc
        ws.sort(Comparator.comparingInt(String::length)
                          .thenComparing(Comparator.naturalOrder())); // length, then alpha

        // Sort 2D array: by col 0 asc, then col 1 asc
        int[][] pairs = {{3,2},{1,5},{1,2}};
        Arrays.sort(pairs, (p, q) -> p[0] != q[0] ? p[0] - q[0] : p[1] - q[1]);


        // ════════════════════════════════════════════════════
        // 14. MATH UTILITIES
        // ════════════════════════════════════════════════════
        Math.max(3, 7);          // 7
        Math.min(3, 7);          // 3
        Math.abs(-5);            // 5
        Math.pow(2, 10);         // 1024.0
        Math.sqrt(16);           // 4.0
        Math.floor(3.9);         // 3.0
        Math.ceil(3.1);          // 4.0
        Math.round(3.5);         // 4L (long)

        Integer.parseInt("42");          // String → int
        Integer.toString(42);            // int → String
        Integer.toBinaryString(10);      // "1010"
        Integer.bitCount(7);             // 3  (number of set bits)
        Character.isLetter('a');         // true
        Character.isDigit('3');          // true
        Character.isLetterOrDigit('_');  // false
        Character.toLowerCase('A');      // 'a'
        int charIdx = 'a' - 'a';         // 0  — letter → index trick


        // ════════════════════════════════════════════════════
        // 15. TYPE CONVERSIONS
        // ════════════════════════════════════════════════════

        // String ↔ int
        int    num1 = Integer.parseInt("123");   // "123" → 123
        String str1 = Integer.toString(123);     // 123 → "123"
        String str2 = String.valueOf(123);       // same

        // int ↔ char
        char c2  = (char)('a' + 3);             // 'd'
        int  idx2 = 'd' - 'a';                  // 3

        // int ↔ double (cast before dividing!)
        double dbl   = (double) 5 / 2;          // 2.5  ← cast first
        int    trunc = (int) 3.99;              // 3

        // int[] ↔ List<Integer>
        int[] prim = {1, 2, 3};
        List<Integer> fromPrim = Arrays.stream(prim).boxed().collect(Collectors.toList());
        int[] backToPrim = fromPrim.stream().mapToInt(Integer::intValue).toArray();

        // Integer[] ↔ List<Integer>
        Integer[] boxArr = {1, 2, 3};
        List<Integer> fromBoxed = new ArrayList<>(Arrays.asList(boxArr));
        Integer[] backToBox = fromBoxed.toArray(new Integer[0]);

        // String ↔ char[]
        char[]  cArr     = "hello".toCharArray();
        String  fromCArr = new String(cArr);


        // ════════════════════════════════════════════════════
        // 16. NULL SAFETY & OPTIONAL
        // ════════════════════════════════════════════════════
        String maybeNull = null;
        if (maybeNull != null) System.out.println(maybeNull.length());

        Optional<String> opt = Optional.ofNullable(maybeNull);
        opt.ifPresent(System.out::println);              // runs only if non-null
        String safe = opt.orElse("default");             // "default"
        String lazy = opt.orElseGet(() -> "computed");
        // opt.orElseThrow(() -> new RuntimeException("missing!"));

        System.out.println("\n✓ All syntax demonstrated.");
    }


    // ════════════════════════════════════════════════════
    // 11. METHODS
    // ════════════════════════════════════════════════════

    // Basic static method
    static int add(int a, int b) { return a + b; }

    // Void — returns nothing
    static void greet(String name) { System.out.println("Hello, " + name); }

    // Overloaded — same name, different params
    static double add(double a, double b) { return a + b; }

    // Varargs — any number of ints
    static int sum(int... nums) {
        int total = 0;
        for (int n : nums) total += n;
        return total;
    }

    // Recursive
    static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    // Return multiple values via array
    static int[] minMax(int[] arr) {
        int min = arr[0], max = arr[0];
        for (int n : arr) { min = Math.min(min, n); max = Math.max(max, n); }
        return new int[]{min, max};
    }
}


// ════════════════════════════════════════════════════════════════════
// QUICK REFERENCE CARD  —  memorize before any interview
// ════════════════════════════════════════════════════════════════════
//
// HOW TO INITIALIZE:
//   int[]            int[] arr = {1,2,3};   or   new int[n]
//   String           String s = "hello";
//   ArrayList        List<Integer> list = new ArrayList<>();
//   LinkedList       LinkedList<Integer> ll = new LinkedList<>();
//   HashMap          Map<String,Integer> map = new HashMap<>();
//   HashSet          Set<Integer> set = new HashSet<>();
//   Stack            Deque<Integer> stack = new ArrayDeque<>();
//   Queue            Queue<Integer> q = new LinkedList<>();
//   PriorityQueue    PriorityQueue<Integer> pq = new PriorityQueue<>();
//   Max PQ           new PriorityQueue<>(Collections.reverseOrder());
//   StringBuilder    StringBuilder sb = new StringBuilder();
//
// HOW TO ACCESS VALUES:
//   array            arr[i]
//   ArrayList        list.get(i)
//   LinkedList       ll.getFirst()  /  ll.getLast()  /  ll.get(i)
//   HashMap          map.get(key)   /  map.getOrDefault(key, 0)
//   Stack top        stack.peek()   /  stack.pop()
//   Queue front      queue.peek()   /  queue.poll()
//   PQ top           pq.peek()      /  pq.poll()
//   String char      s.charAt(i)
//
// SIZE & EMPTY CHECK:
//   array            arr.length           ← FIELD, no parentheses
//   String           s.length()           ← METHOD, has parentheses
//   Collection       list/map/set.size()
//   empty            list.isEmpty()
//
// COMMON GOTCHAS (interview killers):
//   ✗  arr.length()    → arrays use .length with NO parentheses
//   ✗  s1 == s2        → compares references, not content
//   ✓  s1.equals(s2)   → always use for String comparison
//   ✗  (int)(5/2)      → 5/2 is integer division (=2) BEFORE the cast
//   ✓  (double)5 / 2   → cast first, THEN divide → 2.5
//   ✗  list.remove(1)  → removes element at INDEX 1
//   ✓  list.remove(Integer.valueOf(1))  → removes VALUE 1
//   ✗  new Stack<>()   → legacy class, avoid
//   ✓  new ArrayDeque<>() for both stack and queue
//
// LOOP CHEATSHEET:
//   index            for (int i = 0; i < n; i++)
//   reverse          for (int i = n-1; i >= 0; i--)
//   for-each array   for (int x : arr)
//   for-each list    for (String s : list)
//   map entries      for (Map.Entry<K,V> e : map.entrySet())
//   while            while (condition) { ... }
//
// SORTING ONE-LINERS:
//   int[] asc        Arrays.sort(arr)
//   Integer[] desc   Arrays.sort(arr, Comparator.reverseOrder())
//   List asc         Collections.sort(list)
//   List by field    list.sort(Comparator.comparingInt(String::length))
//   2D by col 0      Arrays.sort(arr2d, (a,b) -> a[0] - b[0])
// ════════════════════════════════════════════════════════════════════