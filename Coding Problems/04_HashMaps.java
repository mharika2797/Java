import java.util.*;
import java.util.stream.*;

/**
 * PATTERN: HashMap
 *
 * Problems:
 *  1. Frequency count of characters
 *  2. Two sum using HashMap
 *  3. Group anagrams together
 *  4. Longest consecutive sequence
 *  5. Subarray with sum = 0
 *  6. Find duplicate in array
 *  7. Word frequency count
 *  8. LRU Cache (LinkedHashMap trick)
 */
class HashMaps {

    // 1. Character frequency
    // Input: "aabbccc"  → Output: {a=2, b=2, c=3}
    static Map<Character, Integer> charFrequency(String s) {
        Map<Character, Integer> map = new LinkedHashMap<>();
        for (char c : s.toCharArray()) map.merge(c, 1, Integer::sum);
        return map;
    }

    // 2. Two sum → return indices
    // Input: [2,7,11,15], k=9  → Output: [0,1]
    static int[] twoSum(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(k - nums[i])) return new int[]{map.get(k - nums[i]), i};
            map.put(nums[i], i);
        }
        return new int[]{};
    }

    // 3. Group anagrams
    // Input: ["eat","tea","tan","ate","nat","bat"]
    // Output: [[eat,tea,ate],[tan,nat],[bat]]
    static List<List<String>> groupAnagrams(String[] words) {
        Map<String, List<String>> map = new HashMap<>();
        for (String w : words) {
            char[] chars = w.toCharArray();
            Arrays.sort(chars);
            map.computeIfAbsent(new String(chars), k -> new ArrayList<>()).add(w);
        }
        return new ArrayList<>(map.values());
    }

    // 4. Longest consecutive sequence
    // Input: [100,4,200,1,3,2]  → Output: 4  (1,2,3,4)
    static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        int max = 0;
        for (int n : set) {
            if (!set.contains(n - 1)) { // start of a sequence
                int cur = n, len = 1;
                while (set.contains(cur + 1)) { cur++; len++; }
                max = Math.max(max, len);
            }
        }
        return max;
    }

    // 5. Find subarrays with sum = 0
    // Input: [3,4,-7,1,3,-4]  → Output: [[3,4,-7],[4,-7,1,3,-4,-1]... ]
    static List<int[]> subarraySumZero(int[] nums) {
        Map<Integer, Integer> prefixIndex = new HashMap<>();
        prefixIndex.put(0, -1);
        List<int[]> result = new ArrayList<>();
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (prefixIndex.containsKey(sum)) result.add(new int[]{prefixIndex.get(sum) + 1, i});
            else prefixIndex.put(sum, i);
        }
        return result;
    }

    // 6. Find the duplicate in array [1..n]
    // Input: [1,3,4,2,2]  → Output: 2
    static int findDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int n : nums) if (!seen.add(n)) return n;
        return -1;
    }

    // 7. Word frequency — top 3 words
    // Input: "the cat sat on the mat the cat"  → Output: {the=3, cat=2, sat=1, ...}
    static Map<String, Long> wordFrequency(String sentence) {
        return Arrays.stream(sentence.split(" "))
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
    }

    // 8. LRU Cache using LinkedHashMap
    static class LRUCache {
        private final int capacity;
        private final LinkedHashMap<Integer, Integer> cache;

        LRUCache(int capacity) {
            this.capacity = capacity;
            this.cache = new LinkedHashMap<>(capacity, 0.75f, true) { // accessOrder=true
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    return size() > capacity;
                }
            };
        }

        int get(int key) { return cache.getOrDefault(key, -1); }
        void put(int key, int value) { cache.put(key, value); }

        @Override public String toString() { return cache.toString(); }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Char Frequency ===");
        // Input: "aabbccc"
        System.out.println(charFrequency("aabbccc"));
        // Output: {a=2, b=2, c=3}

        System.out.println("\n=== 2. Two Sum ===");
        // Input: [2,7,11,15], k=9
        System.out.println(Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)));
        // Output: [0, 1]

        System.out.println("\n=== 3. Group Anagrams ===");
        // Input: ["eat","tea","tan","ate","nat","bat"]
        groupAnagrams(new String[]{"eat","tea","tan","ate","nat","bat"}).forEach(System.out::println);
        // Output: [eat, tea, ate]  [tan, nat]  [bat]

        System.out.println("\n=== 4. Longest Consecutive Sequence ===");
        // Input: [100,4,200,1,3,2]
        System.out.println(longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
        // Output: 4

        System.out.println("\n=== 5. Subarray Sum = 0 (start,end indices) ===");
        // Input: [3,4,-7,1,3,-4]
        subarraySumZero(new int[]{3, 4, -7, 1, 3, -4}).forEach(p -> System.out.println(Arrays.toString(p)));
        // Output: [0,2]  [1,5]  ...

        System.out.println("\n=== 6. Find Duplicate ===");
        // Input: [1,3,4,2,2]
        System.out.println(findDuplicate(new int[]{1, 3, 4, 2, 2}));
        // Output: 2

        System.out.println("\n=== 7. Word Frequency ===");
        // Input: "the cat sat on the mat the cat"
        System.out.println(wordFrequency("the cat sat on the mat the cat"));
        // Output: {the=3, cat=2, sat=1, on=1, mat=1}

        System.out.println("\n=== 8. LRU Cache ===");
        LRUCache lru = new LRUCache(3);
        lru.put(1, 10); lru.put(2, 20); lru.put(3, 30);
        System.out.println("get(1)=" + lru.get(1));  // 10
        lru.put(4, 40);  // evicts 2 (least recently used)
        System.out.println("get(2)=" + lru.get(2));  // -1 (evicted)
        System.out.println("Cache: " + lru);
    }
}
