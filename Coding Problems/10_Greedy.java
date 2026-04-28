import java.util.*;

/**
 * PATTERN: Greedy
 *
 * Core idea: make the locally optimal choice at each step,
 * trusting it leads to the global optimum.
 * Works when: problem has "greedy choice property" + "optimal substructure".
 *
 * Problems:
 *  1. Activity selection (max non-overlapping meetings)
 *  2. Jump game — can you reach the end?
 *  3. Jump game II — minimum jumps to reach end
 *  4. Gas station — can you complete the circuit?
 *  5. Assign cookies — maximize satisfied children
 *  6. Best time to buy and sell stock
 *  7. Fractional knapsack
 *  8. Minimum platforms (trains)
 */
class Greedy {

    // 1. Activity selection — max non-overlapping intervals
    // Input: start=[1,3,0,5,8,5], end=[2,4,6,7,9,9]  → Output: 4
    static int maxActivities(int[] start, int[] end) {
        int n = start.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingInt(i -> end[i])); // sort by end time

        int count = 1, lastEnd = end[idx[0]];
        for (int i = 1; i < n; i++) {
            if (start[idx[i]] >= lastEnd) { count++; lastEnd = end[idx[i]]; }
        }
        return count;
    }

    // 2. Jump game — can reach last index?
    // Input: [2,3,1,1,4]  → Output: true
    // Input: [3,2,1,0,4]  → Output: false
    static boolean canJump(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) return false;  // can't reach this index
            maxReach = Math.max(maxReach, i + nums[i]);
        }
        return true;
    }

    // 3. Jump game II — minimum jumps
    // Input: [2,3,1,1,4]  → Output: 2 (0→1→4)
    static int minJumps(int[] nums) {
        int jumps = 0, curEnd = 0, farthest = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);
            if (i == curEnd) { jumps++; curEnd = farthest; } // must jump here
        }
        return jumps;
    }

    // 4. Gas station — find starting station for full circuit (-1 if impossible)
    // Input: gas=[1,2,3,4,5], cost=[3,4,5,1,2]  → Output: 3
    static int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0, tank = 0, start = 0;
        for (int i = 0; i < gas.length; i++) {
            total += gas[i] - cost[i];
            tank  += gas[i] - cost[i];
            if (tank < 0) { start = i + 1; tank = 0; } // reset start
        }
        return total >= 0 ? start : -1;
    }

    // 5. Assign cookies — max children satisfied
    // Each child needs greed[i], each cookie has size s[j]. Cookie satisfies child if s[j] >= greed[i]
    // Input: greed=[1,2,3], cookies=[1,1]  → Output: 1
    static int assignCookies(int[] greed, int[] cookies) {
        Arrays.sort(greed);
        Arrays.sort(cookies);
        int child = 0, cookie = 0;
        while (child < greed.length && cookie < cookies.length) {
            if (cookies[cookie] >= greed[child]) child++; // satisfied
            cookie++;
        }
        return child;
    }

    // 6. Best time to buy/sell stock — max profit (one transaction)
    // Input: [7,1,5,3,6,4]  → Output: 5 (buy@1, sell@6)
    static int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE, maxProfit = 0;
        for (int p : prices) {
            minPrice  = Math.min(minPrice, p);
            maxProfit = Math.max(maxProfit, p - minPrice);
        }
        return maxProfit;
    }

    // 6b. Max profit — unlimited transactions (buy/sell any number of times)
    // Input: [7,1,5,3,6,4]  → Output: 7 (1+5-3+6-4 = 7... or just sum of gains)
    static int maxProfitMultiple(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++)
            if (prices[i] > prices[i - 1]) profit += prices[i] - prices[i - 1];
        return profit;
    }

    // 7. Fractional knapsack — max value (you can take fractions)
    // Input: weights=[10,20,30], values=[60,100,120], W=50  → Output: 240.0
    static double fractionalKnapsack(int[] weights, int[] values, int W) {
        int n = weights.length;
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare((double) values[b] / weights[b], (double) values[a] / weights[a]));

        double total = 0;
        for (int i : idx) {
            if (W >= weights[i]) { total += values[i]; W -= weights[i]; }
            else { total += (double) values[i] / weights[i] * W; break; }
        }
        return total;
    }

    // 8. Minimum platforms needed at a railway station
    // Input: arrive=[900,940,950,1100,1500,1800], depart=[910,1200,1120,1130,1900,2000]  → Output: 3
    static int minPlatforms(int[] arrive, int[] depart) {
        Arrays.sort(arrive);
        Arrays.sort(depart);
        int platforms = 1, max = 1, i = 1, j = 0;
        while (i < arrive.length && j < arrive.length) {
            if (arrive[i] <= depart[j]) { platforms++; i++; }
            else { platforms--; j++; }
            max = Math.max(max, platforms);
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Activity Selection ===");
        // Input: start=[1,3,0,5,8,5], end=[2,4,6,7,9,9]
        System.out.println(maxActivities(new int[]{1,3,0,5,8,5}, new int[]{2,4,6,7,9,9}));
        // Output: 4

        System.out.println("\n=== 2. Can Jump ===");
        System.out.println(canJump(new int[]{2, 3, 1, 1, 4})); // true
        System.out.println(canJump(new int[]{3, 2, 1, 0, 4})); // false

        System.out.println("\n=== 3. Min Jumps ===");
        // Input: [2,3,1,1,4]
        System.out.println(minJumps(new int[]{2, 3, 1, 1, 4})); // 2

        System.out.println("\n=== 4. Gas Station ===");
        // Input: gas=[1,2,3,4,5], cost=[3,4,5,1,2]
        System.out.println(canCompleteCircuit(new int[]{1,2,3,4,5}, new int[]{3,4,5,1,2}));
        // Output: 3

        System.out.println("\n=== 5. Assign Cookies ===");
        // Input: greed=[1,2,3], cookies=[1,1]
        System.out.println(assignCookies(new int[]{1,2,3}, new int[]{1,1}));
        // Output: 1

        System.out.println("\n=== 6a. Max Profit (one trade) ===");
        // Input: [7,1,5,3,6,4]
        System.out.println(maxProfit(new int[]{7, 1, 5, 3, 6, 4}));            // 5

        System.out.println("\n=== 6b. Max Profit (unlimited trades) ===");
        System.out.println(maxProfitMultiple(new int[]{7, 1, 5, 3, 6, 4}));    // 7

        System.out.println("\n=== 7. Fractional Knapsack W=50 ===");
        // Input: weights=[10,20,30], values=[60,100,120]
        System.out.println(fractionalKnapsack(new int[]{10,20,30}, new int[]{60,100,120}, 50));
        // Output: 240.0

        System.out.println("\n=== 8. Minimum Platforms ===");
        // Input: arrive=[900,940,950,1100,1500,1800], depart=[910,1200,1120,1130,1900,2000]
        System.out.println(minPlatforms(
                new int[]{900,940,950,1100,1500,1800},
                new int[]{910,1200,1120,1130,1900,2000}));
        // Output: 3
    }
}
