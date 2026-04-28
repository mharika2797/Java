import java.util.*;

/**
 * PATTERN: Two Pointers
 *
 * Core idea: use two indices (left/right or slow/fast) moving toward each other
 * or in the same direction to reduce O(n²) to O(n).
 *
 * Problems:
 *  1. Two sum in sorted array
 *  2. Remove duplicates from sorted array (in-place)
 *  3. Container with most water
 *  4. Three sum (triplets that sum to 0)
 *  5. Trapping rain water
 *  6. Sort array of 0s, 1s, 2s (Dutch national flag)
 *  7. Palindrome check using two pointers
 *  8. Merge two sorted arrays
 */
class TwoPointers {

    // 1. Two sum in sorted array — return 1-based indices
    // Input: [1,2,3,4,6], target=6  → Output: [2,5]  (2+4)
    static int[] twoSumSorted(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int sum = nums[l] + nums[r];
            if (sum == target) return new int[]{l + 1, r + 1};
            else if (sum < target) l++;
            else r--;
        }
        return new int[]{};
    }

    // 2. Remove duplicates from sorted array (in-place), return new length
    // Input: [1,1,2,3,3]  → Output: length=3, array=[1,2,3,...]
    static int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;
        int write = 1;
        for (int read = 1; read < nums.length; read++) {
            if (nums[read] != nums[write - 1]) nums[write++] = nums[read];
        }
        return write;
    }

    // 3. Container with most water
    // Input: [1,8,6,2,5,4,8,3,7]  → Output: 49
    static int maxWater(int[] height) {
        int l = 0, r = height.length - 1, max = 0;
        while (l < r) {
            max = Math.max(max, Math.min(height[l], height[r]) * (r - l));
            if (height[l] < height[r]) l++;
            else r--;
        }
        return max;
    }

    // 4. Three sum — all unique triplets that sum to 0
    // Input: [-1,0,1,2,-1,-4]  → Output: [[-1,-1,2],[-1,0,1]]
    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue; // skip duplicates
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++; r--;
                } else if (sum < 0) l++;
                else r--;
            }
        }
        return result;
    }

    // 5. Trapping rain water
    // Input: [0,1,0,2,1,0,1,3,2,1,2,1]  → Output: 6
    static int trapRainWater(int[] height) {
        int l = 0, r = height.length - 1;
        int leftMax = 0, rightMax = 0, water = 0;
        while (l < r) {
            if (height[l] < height[r]) {
                if (height[l] >= leftMax) leftMax = height[l];
                else water += leftMax - height[l];
                l++;
            } else {
                if (height[r] >= rightMax) rightMax = height[r];
                else water += rightMax - height[r];
                r--;
            }
        }
        return water;
    }

    // 6. Sort 0s, 1s, 2s (Dutch national flag)
    // Input: [2,0,2,1,1,0]  → Output: [0,0,1,1,2,2]
    static void sortColors(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;
        while (mid <= high) {
            if (nums[mid] == 0)      { swap(nums, low++, mid++); }
            else if (nums[mid] == 1) { mid++; }
            else                     { swap(nums, mid, high--); }
        }
    }
    static void swap(int[] arr, int i, int j) { int t = arr[i]; arr[i] = arr[j]; arr[j] = t; }

    // 7. Valid palindrome (ignore non-alphanumeric)
    // Input: "A man, a plan, a canal: Panama"  → Output: true
    static boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
            while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
            if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) return false;
            l++; r--;
        }
        return true;
    }

    // 8. Merge two sorted arrays into one sorted array
    // Input: [1,3,5], [2,4,6]  → Output: [1,2,3,4,5,6]
    static int[] mergeSorted(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) result[k++] = a[i] < b[j] ? a[i++] : b[j++];
        while (i < a.length) result[k++] = a[i++];
        while (j < b.length) result[k++] = b[j++];
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Two Sum Sorted ===");
        // Input: [1,2,3,4,6], target=6
        System.out.println(Arrays.toString(twoSumSorted(new int[]{1, 2, 3, 4, 6}, 6)));
        // Output: [2, 5]

        System.out.println("\n=== 2. Remove Duplicates ===");
        // Input: [1,1,2,3,3]
        int[] arr = {1, 1, 2, 3, 3};
        int len = removeDuplicates(arr);
        System.out.println("Length: " + len + ", Array: " + Arrays.toString(Arrays.copyOf(arr, len)));
        // Output: Length: 3, Array: [1, 2, 3]

        System.out.println("\n=== 3. Container with Most Water ===");
        // Input: [1,8,6,2,5,4,8,3,7]
        System.out.println(maxWater(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
        // Output: 49

        System.out.println("\n=== 4. Three Sum ===");
        // Input: [-1,0,1,2,-1,-4]
        System.out.println(threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
        // Output: [[-1, -1, 2], [-1, 0, 1]]

        System.out.println("\n=== 5. Trap Rain Water ===");
        // Input: [0,1,0,2,1,0,1,3,2,1,2,1]
        System.out.println(trapRainWater(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        // Output: 6

        System.out.println("\n=== 6. Sort 0s 1s 2s ===");
        // Input: [2,0,2,1,1,0]
        int[] colors = {2, 0, 2, 1, 1, 0};
        sortColors(colors);
        System.out.println(Arrays.toString(colors));
        // Output: [0, 0, 1, 1, 2, 2]

        System.out.println("\n=== 7. Valid Palindrome ===");
        // Input: "A man, a plan, a canal: Panama"
        System.out.println(isPalindrome("A man, a plan, a canal: Panama")); // true
        System.out.println(isPalindrome("race a car"));                      // false

        System.out.println("\n=== 8. Merge Sorted Arrays ===");
        // Input: [1,3,5], [2,4,6]
        System.out.println(Arrays.toString(mergeSorted(new int[]{1, 3, 5}, new int[]{2, 4, 6})));
        // Output: [1, 2, 3, 4, 5, 6]
    }
}
