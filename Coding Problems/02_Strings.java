import java.util.*;

/**
 * PATTERN: Strings
 *
 * Problems:
 *  1. Reverse a string
 *  2. Check if palindrome
 *  3. Check if two strings are anagrams
 *  4. First non-repeating character
 *  5. Longest substring without repeating characters
 *  6. Count and say / run-length encoding
 *  7. Valid parentheses
 *  8. Longest common prefix
 *  9. String compression ("aabbbcc" → "a2b3c2")
 * 10. Check if string is a rotation of another
 */
class Strings {

    // 1. Reverse a string
    // Input: "hello"  → Output: "olleh"
    static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    // 2. Palindrome check
    // Input: "racecar"  → Output: true
    static boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            if (s.charAt(l++) != s.charAt(r--)) return false;
        }
        return true;
    }

    // 3. Anagram check
    // Input: "listen", "silent"  → Output: true
    static boolean isAnagram(String a, String b) {
        if (a.length() != b.length()) return false;
        int[] freq = new int[26];
        for (char c : a.toCharArray()) freq[c - 'a']++;
        for (char c : b.toCharArray()) if (--freq[c - 'a'] < 0) return false;
        return true;
    }

    // 4. First non-repeating character — return index
    // Input: "leetcode"  → Output: 0 ('l')
    static int firstUnique(String s) {
        int[] freq = new int[26];
        for (char c : s.toCharArray()) freq[c - 'a']++;
        for (int i = 0; i < s.length(); i++) if (freq[s.charAt(i) - 'a'] == 1) return i;
        return -1;
    }

    // 5. Longest substring without repeating characters — sliding window
    // Input: "abcabcbb"  → Output: 3 ("abc")
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

    // 6. Run-length encoding
    // Input: "aabbbcccc"  → Output: "a2b3c4"
    static String compress(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            int count = 0;
            while (i < s.length() && s.charAt(i) == c) { i++; count++; }
            sb.append(c).append(count);
        }
        return sb.toString();
    }

    // 7. Valid parentheses
    // Input: "{[()]}"  → Output: true
    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') stack.push(c);
            else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if (c == ')' && top != '(') return false;
                if (c == '}' && top != '{') return false;
                if (c == ']' && top != '[') return false;
            }
        }
        return stack.isEmpty();
    }

    // 8. Longest common prefix
    // Input: ["flower","flow","flight"]  → Output: "fl"
    static String longestCommonPrefix(String[] words) {
        if (words.length == 0) return "";
        String prefix = words[0];
        for (String w : words) while (!w.startsWith(prefix)) prefix = prefix.substring(0, prefix.length() - 1);
        return prefix;
    }

    // 9. String compression (only compress if shorter)
    // Input: "aabbbcc"  → Output: "a2b3c2"
    static String stringCompression(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i); int count = 0;
            while (i < s.length() && s.charAt(i) == c) { i++; count++; }
            sb.append(c);
            if (count > 1) sb.append(count);
        }
        return sb.length() < s.length() ? sb.toString() : s;
    }

    // 10. Check if s2 is a rotation of s1
    // Input: "abcde", "cdeab"  → Output: true
    static boolean isRotation(String s1, String s2) {
        return s1.length() == s2.length() && (s1 + s1).contains(s2);
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Reverse ===");
        System.out.println(reverse("hello"));                     // olleh

        System.out.println("\n=== 2. Palindrome ===");
        System.out.println(isPalindrome("racecar"));              // true
        System.out.println(isPalindrome("hello"));                // false

        System.out.println("\n=== 3. Anagram ===");
        System.out.println(isAnagram("listen", "silent"));        // true
        System.out.println(isAnagram("hello", "world"));          // false

        System.out.println("\n=== 4. First Unique Char ===");
        System.out.println(firstUnique("leetcode"));              // 0
        System.out.println(firstUnique("aabb"));                  // -1

        System.out.println("\n=== 5. Longest Substring No Repeat ===");
        System.out.println(longestUniqueSubstring("abcabcbb"));   // 3
        System.out.println(longestUniqueSubstring("pwwkew"));     // 3

        System.out.println("\n=== 6. Run-length Encoding ===");
        System.out.println(compress("aabbbcccc"));                // a2b3c4

        System.out.println("\n=== 7. Valid Parentheses ===");
        System.out.println(isValid("{[()]}"));                    // true
        System.out.println(isValid("{[(])}"));                    // false

        System.out.println("\n=== 8. Longest Common Prefix ===");
        System.out.println(longestCommonPrefix(new String[]{"flower","flow","flight"})); // fl

        System.out.println("\n=== 9. String Compression ===");
        System.out.println(stringCompression("aabbbcc"));         // a2b3c2

        System.out.println("\n=== 10. Is Rotation ===");
        System.out.println(isRotation("abcde", "cdeab"));         // true
        System.out.println(isRotation("abcde", "abced"));         // false
    }
}
