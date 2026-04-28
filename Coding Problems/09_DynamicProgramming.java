import java.util.*;

/**
 * PATTERN: Dynamic Programming (DP)
 *
 * Core idea: break problem into overlapping subproblems,
 * store results to avoid recomputation (memoization / tabulation).
 *
 * Problems:
 *  1. Fibonacci (memoized)
 *  2. Climbing stairs (1 or 2 steps)
 *  3. House robber (no two adjacent houses)
 *  4. 0/1 Knapsack
 *  5. Longest Common Subsequence (LCS)
 *  6. Longest Increasing Subsequence (LIS)
 *  7. Coin change (minimum coins)
 *  8. Edit distance (Levenshtein)
 *  9. Unique paths in grid
 * 10. Palindromic substrings count
 */
class DynamicProgramming {

    // 1. Fibonacci — memoized top-down
    // Input: n=7  → Output: 13
    static Map<Integer, Long> memo = new HashMap<>();
    static long fib(int n) {
        if (n <= 1) return n;
        if (memo.containsKey(n)) return memo.get(n);
        long result = fib(n - 1) + fib(n - 2);
        memo.put(n, result);
        return result;
    }

    // 2. Climbing stairs — ways to reach step n using 1 or 2 steps
    // Input: n=5  → Output: 8
    static int climbStairs(int n) {
        if (n <= 2) return n;
        int a = 1, b = 2;
        for (int i = 3; i <= n; i++) { int c = a + b; a = b; b = c; }
        return b;
    }

    // 3. House robber — max money, no two adjacent
    // Input: [2,7,9,3,1]  → Output: 12 (2+9+1)
    static int rob(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        int prev2 = 0, prev1 = 0;
        for (int n : nums) { int cur = Math.max(prev1, prev2 + n); prev2 = prev1; prev1 = cur; }
        return prev1;
    }

    // 4. 0/1 Knapsack — max value with weight limit W
    // Input: weights=[1,3,4,5], values=[1,4,5,7], W=7  → Output: 9
    static int knapsack(int[] weights, int[] values, int W) {
        int n = weights.length;
        int[][] dp = new int[n + 1][W + 1];
        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= W; w++) {
                dp[i][w] = dp[i - 1][w]; // skip item
                if (weights[i - 1] <= w)
                    dp[i][w] = Math.max(dp[i][w], dp[i - 1][w - weights[i - 1]] + values[i - 1]);
            }
        }
        return dp[n][W];
    }

    // 5. Longest Common Subsequence
    // Input: "abcde", "ace"  → Output: 3 ("ace")
    static int lcs(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                dp[i][j] = a.charAt(i-1) == b.charAt(j-1) ? dp[i-1][j-1] + 1 : Math.max(dp[i-1][j], dp[i][j-1]);
        return dp[m][n];
    }

    // 6. Longest Increasing Subsequence (LIS)
    // Input: [10,9,2,5,3,7,101,18]  → Output: 4 (2,3,7,101 or 2,5,7,101)
    static int lis(int[] nums) {
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int max = 1;
        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++)
                if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
            max = Math.max(max, dp[i]);
        }
        return max;
    }

    // 7. Coin change — minimum coins to make amount
    // Input: coins=[1,5,6,9], amount=11  → Output: 2 (5+6)
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // init to "infinity"
        dp[0] = 0;
        for (int i = 1; i <= amount; i++)
            for (int coin : coins)
                if (coin <= i) dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        return dp[amount] > amount ? -1 : dp[amount];
    }

    // 8. Edit distance (min insert/delete/replace to convert s1 → s2)
    // Input: "horse", "ros"  → Output: 3
    static int editDistance(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++)
                dp[i][j] = a.charAt(i-1) == b.charAt(j-1)
                        ? dp[i-1][j-1]
                        : 1 + Math.min(dp[i-1][j-1], Math.min(dp[i-1][j], dp[i][j-1]));
        return dp[m][n];
    }

    // 9. Unique paths in m×n grid (move only right or down)
    // Input: m=3, n=7  → Output: 28
    static int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int[] row : dp) Arrays.fill(row, 1); // first row and column are all 1
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                dp[i][j] = dp[i-1][j] + dp[i][j-1];
        return dp[m-1][n-1];
    }

    // 10. Count palindromic substrings
    // Input: "abc"  → Output: 3  ("a","b","c")
    // Input: "aaa"  → Output: 6  ("a","a","a","aa","aa","aaa")
    static int countPalindromes(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            count += expand(s, i, i);     // odd length
            count += expand(s, i, i + 1); // even length
        }
        return count;
    }
    static int expand(String s, int l, int r) {
        int count = 0;
        while (l >= 0 && r < s.length() && s.charAt(l--) == s.charAt(r++)) count++;
        return count;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Fibonacci(7) ===");
        System.out.println(fib(7));                                                  // 13

        System.out.println("\n=== 2. Climb Stairs(5) ===");
        System.out.println(climbStairs(5));                                          // 8

        System.out.println("\n=== 3. House Robber ===");
        System.out.println(rob(new int[]{2, 7, 9, 3, 1}));                          // 12

        System.out.println("\n=== 4. 0/1 Knapsack W=7 ===");
        System.out.println(knapsack(new int[]{1,3,4,5}, new int[]{1,4,5,7}, 7));    // 9

        System.out.println("\n=== 5. LCS ===");
        System.out.println(lcs("abcde", "ace"));                                    // 3

        System.out.println("\n=== 6. LIS ===");
        System.out.println(lis(new int[]{10, 9, 2, 5, 3, 7, 101, 18}));            // 4

        System.out.println("\n=== 7. Coin Change amount=11 ===");
        System.out.println(coinChange(new int[]{1, 5, 6, 9}, 11));                  // 2

        System.out.println("\n=== 8. Edit Distance ===");
        System.out.println(editDistance("horse", "ros"));                            // 3

        System.out.println("\n=== 9. Unique Paths 3x7 ===");
        System.out.println(uniquePaths(3, 7));                                       // 28

        System.out.println("\n=== 10. Palindromic Substrings ===");
        System.out.println(countPalindromes("abc"));                                 // 3
        System.out.println(countPalindromes("aaa"));                                 // 6
    }
}
