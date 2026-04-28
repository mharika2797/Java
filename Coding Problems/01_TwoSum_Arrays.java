import java.util.*;

/**
 * PATTERN: Arrays — Two Sum & Variations
 *
 * Problems:
 *  1. Two Sum — find indices of two numbers that add up to k
 *  2. Two Sum II — sorted array, find pair using two pointers
 *  3. Find all pairs with sum = k
 *  4. Subarray with sum = k (Kadane variant)
 *  5. Max subarray sum (Kadane's algorithm)
 *  6. Find missing number in [1..n]
 *  7. Move all zeros to end
 *  8. Rotate array by k steps
 */
class TwoSum_Arrays {

    // 1. Two Sum — return indices
    // Input: [2,7,11,15], k=9  → Output: [0,1]
    static int[] twoSum(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>(); // value → index
        for (int i = 0; i < nums.length; i++) {
            int complement = k - nums[i];
            if (map.containsKey(complement)) return new int[]{map.get(complement), i};
            map.put(nums[i], i);
        }
        return new int[]{};
    }

    // 2. Two Sum II — sorted array, return 1-based indices
    // Input: [1,2,3,4,6], k=6  → Output: [2,5]  (2+4 or 3+3... first valid pair)
    static int[] twoSumSorted(int[] nums, int k) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int sum = nums[left] + nums[right];
            if (sum == k)        return new int[]{left + 1, right + 1};
            else if (sum < k)    left++;
            else                 right--;
        }
        return new int[]{};
    }

    // 3. Find all unique pairs with sum = k
    // Input: [1,2,3,4,5], k=5  → Output: [(1,4),(2,3)]
    static List<int[]> allPairs(int[] nums, int k) {
        Set<Integer> seen = new HashSet<>();
        Set<String> used = new HashSet<>();
        List<int[]> result = new ArrayList<>();
        for (int n : nums) {
            int complement = k - n;
            if (seen.contains(complement)) {
                int a = Math.min(n, complement), b = Math.max(n, complement);
                if (used.add(a + "," + b)) result.add(new int[]{a, b});
            }
            seen.add(n);
        }
        return result;
    }

    // 4. Count subarrays with sum = k
    // Input: [1,1,1], k=2  → Output: 2
    static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        int sum = 0, count = 0;
        for (int n : nums) {
            sum += n;
            count += prefixCount.getOrDefault(sum - k, 0);
            prefixCount.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // 5. Max subarray sum — Kadane's algorithm
    // Input: [-2,1,-3,4,-1,2,1,-5,4]  → Output: 6 (subarray [4,-1,2,1])
    static int maxSubarray(int[] nums) {
        int max = nums[0], cur = nums[0];
        for (int i = 1; i < nums.length; i++) {
            cur = Math.max(nums[i], cur + nums[i]);
            max = Math.max(max, cur);
        }
        return max;
    }

    // 6. Find missing number in array [1..n]
    // Input: [3,0,1]  → Output: 2
    static int missingNumber(int[] nums) {
        int n = nums.length;
        int expected = n * (n + 1) / 2;
        int actual = Arrays.stream(nums).sum();
        return expected - actual;
    }

    // 7. Move zeros to end (in-place)
    // Input: [0,1,0,3,12]  → Output: [1,3,12,0,0]
    static void moveZeros(int[] nums) {
        int insert = 0;
        for (int n : nums) if (n != 0) nums[insert++] = n;
        while (insert < nums.length) nums[insert++] = 0;
    }

    // 8. Rotate array right by k steps
    // Input: [1,2,3,4,5,6,7], k=3  → Output: [5,6,7,1,2,3,4]
    static void rotate(int[] nums, int k) {
        k %= nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }
    static void reverse(int[] nums, int l, int r) {
        while (l < r) { int tmp = nums[l]; nums[l++] = nums[r]; nums[r--] = tmp; }
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Two Sum ===");
        // Input: [2,7,11,15], k=9
        System.out.println(Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)));
        // Output: [0, 1]

        System.out.println("\n=== 2. Two Sum II (sorted) ===");
        // Input: [1,2,4,6], k=6
        System.out.println(Arrays.toString(twoSumSorted(new int[]{1, 2, 4, 6}, 6)));
        // Output: [2, 4]  (2-based: nums[1]=2, nums[3]=4)

        System.out.println("\n=== 3. All Pairs with sum=5 ===");
        // Input: [1,2,3,4,5], k=5
        allPairs(new int[]{1, 2, 3, 4, 5}, 5).forEach(p -> System.out.println(Arrays.toString(p)));
        // Output: [1,4]  [2,3]

        System.out.println("\n=== 4. Count Subarrays with sum=2 ===");
        // Input: [1,1,1], k=2
        System.out.println(subarraySum(new int[]{1, 1, 1}, 2));
        // Output: 2

        System.out.println("\n=== 5. Max Subarray Sum ===");
        // Input: [-2,1,-3,4,-1,2,1,-5,4]
        System.out.println(maxSubarray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
        // Output: 6

        System.out.println("\n=== 6. Missing Number ===");
        // Input: [3,0,1]
        System.out.println(missingNumber(new int[]{3, 0, 1}));
        // Output: 2

        System.out.println("\n=== 7. Move Zeros ===");
        // Input: [0,1,0,3,12]
        int[] arr = {0, 1, 0, 3, 12};
        moveZeros(arr);
        System.out.println(Arrays.toString(arr));
        // Output: [1, 3, 12, 0, 0]

        System.out.println("\n=== 8. Rotate Array by k=3 ===");
        // Input: [1,2,3,4,5,6,7]
        int[] arr2 = {1, 2, 3, 4, 5, 6, 7};
        rotate(arr2, 3);
        System.out.println(Arrays.toString(arr2));
        // Output: [5, 6, 7, 1, 2, 3, 4]
    }
}
