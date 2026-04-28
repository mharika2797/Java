import java.util.*;

/**
 * PATTERN: Sliding Window
 *
 * Core idea: maintain a window [left, right] over the array/string.
 * Expand right to grow, shrink left to satisfy constraints.
 * Avoids nested loops — reduces O(n²) to O(n).
 *
 * Fixed window: window size k is constant.
 * Variable window: window shrinks/grows based on a condition.
 *
 * Problems:
 *  1. Max sum subarray of size k (fixed)
 *  2. Longest substring with at most k distinct chars (variable)
 *  3. Minimum window substring containing all chars of t
 *  4. Longest substring without repeating characters
 *  5. Find all anagrams in a string
 *  6. Max consecutive ones after flipping at most k zeros
 */
class SlidingWindow {

    // 1. Max sum of subarray of size k (fixed window)
    // Input: [2,1,5,1,3,2], k=3  → Output: 9  (5+1+3)
    static int maxSumSubarray(int[] nums, int k) {
        int sum = 0;
        for (int i = 0; i < k; i++) sum += nums[i]; // first window
        int max = sum;
        for (int i = k; i < nums.length; i++) {
            sum += nums[i] - nums[i - k]; // slide: add new, remove old
            max = Math.max(max, sum);
        }
        return max;
    }

    // 2. Longest substring with at most k distinct characters (variable)
    // Input: "eceba", k=2  → Output: 3  ("ece")
    static int longestWithKDistinct(String s, int k) {
        Map<Character, Integer> freq = new HashMap<>();
        int left = 0, max = 0;
        for (int right = 0; right < s.length(); right++) {
            freq.merge(s.charAt(right), 1, Integer::sum);
            while (freq.size() > k) {
                char lc = s.charAt(left++);
                freq.merge(lc, -1, Integer::sum);
                if (freq.get(lc) == 0) freq.remove(lc);
            }
            max = Math.max(max, right - left + 1);
        }
        return max;
    }

    // 3. Minimum window substring — smallest window in s containing all chars of t
    // Input: s="ADOBECODEBANC", t="ABC"  → Output: "BANC"
    static String minWindowSubstring(String s, String t) {
        Map<Character, Integer> need = new HashMap<>();
        for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);
        int left = 0, satisfied = 0, minLen = Integer.MAX_VALUE, start = 0;
        Map<Character, Integer> window = new HashMap<>();
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            window.merge(c, 1, Integer::sum);
            if (need.containsKey(c) && window.get(c).equals(need.get(c))) satisfied++;
            while (satisfied == need.size()) {
                if (right - left + 1 < minLen) { minLen = right - left + 1; start = left; }
                char lc = s.charAt(left++);
                window.merge(lc, -1, Integer::sum);
                if (need.containsKey(lc) && window.get(lc) < need.get(lc)) satisfied--;
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(start, start + minLen);
    }

    // 4. Longest substring without repeating characters
    // Input: "abcabcbb"  → Output: 3  ("abc")
    static int longestUniqueSubstring(String s) {
        Set<Character> window = new HashSet<>();
        int left = 0, max = 0;
        for (int right = 0; right < s.length(); right++) {
            while (window.contains(s.charAt(right))) window.remove(s.charAt(left++));
            window.add(s.charAt(right));
            max = Math.max(max, right - left + 1);
        }
        return max;
    }

    // 5. Find all anagrams of p in s — return start indices
    // Input: s="cbaebabacd", p="abc"  → Output: [0, 6]
    static List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) return result;
        int[] pCount = new int[26], wCount = new int[26];
        for (char c : p.toCharArray()) pCount[c - 'a']++;
        for (int i = 0; i < p.length(); i++) wCount[s.charAt(i) - 'a']++;
        if (Arrays.equals(pCount, wCount)) result.add(0);
        for (int i = p.length(); i < s.length(); i++) {
            wCount[s.charAt(i) - 'a']++;
            wCount[s.charAt(i - p.length()) - 'a']--;
            if (Arrays.equals(pCount, wCount)) result.add(i - p.length() + 1);
        }
        return result;
    }

    // 6. Max consecutive 1s after flipping at most k zeros
    // Input: [1,1,1,0,0,0,1,1,1,1,0], k=2  → Output: 6
    static int maxConsecutiveOnes(int[] nums, int k) {
        int left = 0, zeros = 0, max = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) zeros++;
            while (zeros > k) { if (nums[left++] == 0) zeros--; }
            max = Math.max(max, right - left + 1);
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Max Sum Subarray k=3 ===");
        // Input: [2,1,5,1,3,2]
        System.out.println(maxSumSubarray(new int[]{2, 1, 5, 1, 3, 2}, 3));     // 9

        System.out.println("\n=== 2. Longest with K=2 Distinct ===");
        // Input: "eceba"
        System.out.println(longestWithKDistinct("eceba", 2));                    // 3

        System.out.println("\n=== 3. Minimum Window Substring ===");
        // Input: s="ADOBECODEBANC", t="ABC"
        System.out.println(minWindowSubstring("ADOBECODEBANC", "ABC"));         // BANC

        System.out.println("\n=== 4. Longest Unique Substring ===");
        // Input: "abcabcbb"
        System.out.println(longestUniqueSubstring("abcabcbb"));                 // 3

        System.out.println("\n=== 5. Find Anagrams ===");
        // Input: s="cbaebabacd", p="abc"
        System.out.println(findAnagrams("cbaebabacd", "abc"));                  // [0, 6]

        System.out.println("\n=== 6. Max Consecutive 1s (flip k=2 zeros) ===");
        // Input: [1,1,1,0,0,0,1,1,1,1,0]
        System.out.println(maxConsecutiveOnes(new int[]{1,1,1,0,0,0,1,1,1,1,0}, 2)); // 6
    }
}
