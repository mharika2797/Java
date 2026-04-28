import java.util.*;

/**
 * PATTERN: Binary Search
 *
 * Core idea: on a sorted (or monotonic) space, eliminate half the search
 * space each step → O(log n). Template:
 *   left=0, right=n-1
 *   while (left <= right) {
 *       mid = left + (right - left) / 2;   ← avoids overflow
 *       if (condition) return mid;
 *       else if (too small) left = mid + 1;
 *       else right = mid - 1;
 *   }
 *
 * Problems:
 *  1. Classic binary search
 *  2. Find first and last position of element
 *  3. Search in rotated sorted array
 *  4. Find minimum in rotated sorted array
 *  5. Find peak element
 *  6. Koko eating bananas (binary search on answer)
 *  7. Sqrt(x) — integer square root
 */
class BinarySearch {

    // 1. Classic binary search
    // Input: [1,3,5,7,9], target=7  → Output: 3
    static int binarySearch(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) return mid;
            else if (nums[mid] < target) l = mid + 1;
            else r = mid - 1;
        }
        return -1;
    }

    // 2. First and last position of element in sorted array
    // Input: [5,7,7,8,8,10], target=8  → Output: [3,4]
    static int[] searchRange(int[] nums, int target) {
        return new int[]{firstPos(nums, target), lastPos(nums, target)};
    }
    static int firstPos(int[] nums, int t) {
        int l = 0, r = nums.length - 1, pos = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == t) { pos = mid; r = mid - 1; } // keep going left
            else if (nums[mid] < t) l = mid + 1;
            else r = mid - 1;
        }
        return pos;
    }
    static int lastPos(int[] nums, int t) {
        int l = 0, r = nums.length - 1, pos = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == t) { pos = mid; l = mid + 1; } // keep going right
            else if (nums[mid] < t) l = mid + 1;
            else r = mid - 1;
        }
        return pos;
    }

    // 3. Search in rotated sorted array
    // Input: [4,5,6,7,0,1,2], target=0  → Output: 4
    static int searchRotated(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) return mid;
            if (nums[l] <= nums[mid]) { // left half is sorted
                if (nums[l] <= target && target < nums[mid]) r = mid - 1;
                else l = mid + 1;
            } else { // right half is sorted
                if (nums[mid] < target && target <= nums[r]) l = mid + 1;
                else r = mid - 1;
            }
        }
        return -1;
    }

    // 4. Minimum in rotated sorted array
    // Input: [3,4,5,1,2]  → Output: 1
    static int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] > nums[r]) l = mid + 1; // min is in right half
            else r = mid;                           // min is in left half (including mid)
        }
        return nums[l];
    }

    // 5. Find peak element (greater than neighbors)
    // Input: [1,2,3,1]  → Output: 2 (index of 3)
    static int findPeak(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] < nums[mid + 1]) l = mid + 1; // peak is to the right
            else r = mid;
        }
        return l;
    }

    // 6. Koko eating bananas — min speed to eat all piles within h hours
    // Input: piles=[3,6,7,11], h=8  → Output: 4
    static int minEatingSpeed(int[] piles, int h) {
        int l = 1, r = Arrays.stream(piles).max().getAsInt();
        while (l < r) {
            int mid = l + (r - l) / 2;
            int hours = 0;
            for (int p : piles) hours += (p + mid - 1) / mid; // ceil division
            if (hours <= h) r = mid; // might be able to go slower
            else l = mid + 1;        // too slow, need to eat faster
        }
        return l;
    }

    // 7. Integer square root — largest x such that x*x <= n
    // Input: 8  → Output: 2  (sqrt(8)=2.82..., floor=2)
    static int mySqrt(int n) {
        if (n < 2) return n;
        int l = 1, r = n / 2;
        while (l <= r) {
            long mid = l + (r - l) / 2;
            if (mid * mid == n) return (int) mid;
            else if (mid * mid < n) l = (int) mid + 1;
            else r = (int) mid - 1;
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Binary Search target=7 ===");
        // Input: [1,3,5,7,9]
        System.out.println(binarySearch(new int[]{1, 3, 5, 7, 9}, 7));          // 3

        System.out.println("\n=== 2. First and Last Position target=8 ===");
        // Input: [5,7,7,8,8,10]
        System.out.println(Arrays.toString(searchRange(new int[]{5,7,7,8,8,10}, 8))); // [3, 4]

        System.out.println("\n=== 3. Search in Rotated Array target=0 ===");
        // Input: [4,5,6,7,0,1,2]
        System.out.println(searchRotated(new int[]{4, 5, 6, 7, 0, 1, 2}, 0));   // 4

        System.out.println("\n=== 4. Min in Rotated Array ===");
        // Input: [3,4,5,1,2]
        System.out.println(findMin(new int[]{3, 4, 5, 1, 2}));                  // 1

        System.out.println("\n=== 5. Find Peak Element ===");
        // Input: [1,2,3,1]
        System.out.println(findPeak(new int[]{1, 2, 3, 1}));                    // 2

        System.out.println("\n=== 6. Koko Eating Bananas h=8 ===");
        // Input: piles=[3,6,7,11]
        System.out.println(minEatingSpeed(new int[]{3, 6, 7, 11}, 8));          // 4

        System.out.println("\n=== 7. Integer Sqrt(8) ===");
        System.out.println(mySqrt(8));                                           // 2
        System.out.println(mySqrt(16));                                          // 4
    }
}
