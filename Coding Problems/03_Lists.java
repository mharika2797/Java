import java.util.*;
import java.util.stream.*;

/**
 * PATTERN: List / ArrayList
 *
 * Problems:
 *  1. Remove duplicates from list
 *  2. Find intersection of two lists
 *  3. Find union of two lists
 *  4. Sort list of strings by length
 *  5. Group elements by even/odd
 *  6. Flatten a list of lists
 *  7. Find top K frequent elements
 *  8. Sliding window maximum
 */
class Lists {

    // 1. Remove duplicates (preserve order)
    // Input: [1,2,2,3,4,3,5]  → Output: [1,2,3,4,5]
    static List<Integer> removeDuplicates(List<Integer> list) {
        return new ArrayList<>(new LinkedHashSet<>(list));
    }

    // 2. Intersection — elements common to both
    // Input: [1,2,3,4], [2,4,6]  → Output: [2,4]
    static List<Integer> intersection(List<Integer> a, List<Integer> b) {
        Set<Integer> setB = new HashSet<>(b);
        return a.stream().filter(setB::contains).distinct().collect(Collectors.toList());
    }

    // 3. Union — all unique elements from both
    // Input: [1,2,3], [2,3,4]  → Output: [1,2,3,4]
    static List<Integer> union(List<Integer> a, List<Integer> b) {
        Set<Integer> result = new LinkedHashSet<>(a);
        result.addAll(b);
        return new ArrayList<>(result);
    }

    // 4. Sort strings by length, then alphabetically
    // Input: ["banana","apple","fig","kiwi"]  → Output: [fig, kiwi, apple, banana]
    static List<String> sortByLength(List<String> list) {
        return list.stream()
                .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList());
    }

    // 5. Partition into even and odd
    // Input: [1,2,3,4,5,6]  → Output: {true=[2,4,6], false=[1,3,5]}
    static Map<Boolean, List<Integer>> partitionEvenOdd(List<Integer> list) {
        return list.stream().collect(Collectors.partitioningBy(n -> n % 2 == 0));
    }

    // 6. Flatten list of lists
    // Input: [[1,2],[3,4],[5]]  → Output: [1,2,3,4,5]
    static List<Integer> flatten(List<List<Integer>> nested) {
        return nested.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    // 7. Top K frequent elements
    // Input: [1,1,1,2,2,3], k=2  → Output: [1,2]
    static List<Integer> topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);
        return freq.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .limit(k)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 8. Sliding window maximum — max in every window of size k
    // Input: [1,3,-1,-3,5,3,6,7], k=3  → Output: [3,3,5,5,6,7]
    static int[] slidingWindowMax(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>(); // stores indices, front = max index
        for (int i = 0; i < n; i++) {
            while (!dq.isEmpty() && dq.peek() < i - k + 1) dq.poll(); // out of window
            while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast(); // smaller elements useless
            dq.offer(i);
            if (i >= k - 1) result[i - k + 1] = nums[dq.peek()];
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Remove Duplicates ===");
        // Input: [1,2,2,3,4,3,5]
        System.out.println(removeDuplicates(Arrays.asList(1, 2, 2, 3, 4, 3, 5)));
        // Output: [1, 2, 3, 4, 5]

        System.out.println("\n=== 2. Intersection ===");
        // Input: [1,2,3,4], [2,4,6]
        System.out.println(intersection(Arrays.asList(1, 2, 3, 4), Arrays.asList(2, 4, 6)));
        // Output: [2, 4]

        System.out.println("\n=== 3. Union ===");
        // Input: [1,2,3], [2,3,4]
        System.out.println(union(Arrays.asList(1, 2, 3), Arrays.asList(2, 3, 4)));
        // Output: [1, 2, 3, 4]

        System.out.println("\n=== 4. Sort by Length ===");
        // Input: ["banana","apple","fig","kiwi"]
        System.out.println(sortByLength(Arrays.asList("banana", "apple", "fig", "kiwi")));
        // Output: [fig, kiwi, apple, banana]

        System.out.println("\n=== 5. Partition Even/Odd ===");
        // Input: [1,2,3,4,5,6]
        System.out.println(partitionEvenOdd(Arrays.asList(1, 2, 3, 4, 5, 6)));
        // Output: {false=[1, 3, 5], true=[2, 4, 6]}

        System.out.println("\n=== 6. Flatten ===");
        // Input: [[1,2],[3,4],[5]]
        System.out.println(flatten(Arrays.asList(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5))));
        // Output: [1, 2, 3, 4, 5]

        System.out.println("\n=== 7. Top K Frequent ===");
        // Input: [1,1,1,2,2,3], k=2
        System.out.println(topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2));
        // Output: [1, 2]

        System.out.println("\n=== 8. Sliding Window Max ===");
        // Input: [1,3,-1,-3,5,3,6,7], k=3
        System.out.println(Arrays.toString(slidingWindowMax(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)));
        // Output: [3, 3, 5, 5, 6, 7]
    }
}
