# Arrays

## Basics

- Fixed size, same type, contiguous memory.
- Zero-indexed.

```java
int[] arr = new int[5];           // [0,0,0,0,0]
int[] arr = {1, 2, 3, 4, 5};     // initialization
int[][] matrix = new int[3][4];   // 2D array

arr.length        // 5 (field, not method)
arr[0]            // first element
arr[arr.length-1] // last element
```

---

## Common Operations

```java
// Sort
Arrays.sort(arr);                        // ascending, in-place
Arrays.sort(arr, Collections.reverseOrder()); // descending (Integer[] only)

// Search (array must be sorted)
Arrays.binarySearch(arr, key);

// Copy
int[] copy = Arrays.copyOf(arr, arr.length);
int[] partial = Arrays.copyOfRange(arr, 1, 4); // index 1 to 3

// Fill
Arrays.fill(arr, 0);

// Compare
Arrays.equals(arr1, arr2);

// Print
System.out.println(Arrays.toString(arr));       // 1D
System.out.println(Arrays.deepToString(matrix)); // 2D
```

---

## Array → List / List → Array

```java
// Array to List (fixed size — no add/remove)
List<Integer> list = Arrays.asList(1, 2, 3);

// Array to mutable List
List<Integer> list = new ArrayList<>(Arrays.asList(arr));

// List to Array
Integer[] arr = list.toArray(new Integer[0]);
```

---

## 2D Array

```java
int[][] matrix = {{1,2,3},{4,5,6},{7,8,9}};
for (int[] row : matrix) {
    for (int val : row) {
        System.out.print(val + " ");
    }
}
matrix.length     // number of rows
matrix[0].length  // number of columns
```

---

## Common Interview Questions

**Q: Difference between Array and ArrayList?**
| Array | ArrayList |
|---|---|
| Fixed size | Dynamic size |
| Can hold primitives | Only objects (autoboxing) |
| `arr.length` | `list.size()` |
| Faster (no resize overhead) | More flexible |

**Q: How to find duplicates in an array?**
```java
Set<Integer> seen = new HashSet<>();
for (int n : arr) {
    if (!seen.add(n)) System.out.println("Duplicate: " + n);
}
```

**Q: How to find the second largest element?**
```java
Arrays.sort(arr);
return arr[arr.length - 2]; // after removing duplicates ideally
```

**Q: How to rotate an array by k positions?**
Reverse entire array, reverse first k, reverse remaining k to end.

**Q: What happens if you access an index out of bounds?**
`ArrayIndexOutOfBoundsException` at runtime.
