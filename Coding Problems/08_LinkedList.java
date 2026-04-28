import java.util.*;

/**
 * PATTERN: Linked List
 *
 * Problems:
 *  1. Reverse a linked list
 *  2. Detect cycle (Floyd's slow/fast pointer)
 *  3. Find start of cycle
 *  4. Find middle of linked list
 *  5. Merge two sorted linked lists
 *  6. Remove Nth node from end
 *  7. Check if linked list is palindrome
 *  8. Add two numbers (digits stored in reverse order)
 */
class LinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    // Helper: build list from array
    static ListNode build(int... vals) {
        ListNode dummy = new ListNode(0), cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    // Helper: print list
    static String print(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        while (head != null) { sb.append(head.val); if (head.next != null) sb.append("→"); head = head.next; }
        return sb.append("]").toString();
    }

    // 1. Reverse linked list (iterative)
    // Input: 1→2→3→4→5  → Output: 5→4→3→2→1
    static ListNode reverse(ListNode head) {
        ListNode prev = null, cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    // 2. Detect cycle — Floyd's algorithm
    // If slow and fast pointer meet → cycle exists
    // Input: 1→2→3→4→2 (cycle)  → Output: true
    static boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    // 3. Find start of cycle
    // After detecting meeting point: reset one to head, advance both one step at a time
    static ListNode cycleStart(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                slow = head; // reset one to head
                while (slow != fast) { slow = slow.next; fast = fast.next; }
                return slow; // meeting point = cycle start
            }
        }
        return null;
    }

    // 4. Find middle (slow/fast — fast moves 2x)
    // Input: 1→2→3→4→5  → Output: 3
    static ListNode findMiddle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // 5. Merge two sorted linked lists
    // Input: 1→3→5, 2→4→6  → Output: 1→2→3→4→5→6
    static ListNode mergeSorted(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), cur = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) { cur.next = l1; l1 = l1.next; }
            else                  { cur.next = l2; l2 = l2.next; }
            cur = cur.next;
        }
        cur.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }

    // 6. Remove Nth node from end
    // Input: 1→2→3→4→5, n=2  → Output: 1→2→3→5
    static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i <= n; i++) fast = fast.next; // advance fast n+1 steps
        while (fast != null) { slow = slow.next; fast = fast.next; }
        slow.next = slow.next.next; // remove the node
        return dummy.next;
    }

    // 7. Palindrome linked list
    // Input: 1→2→3→2→1  → Output: true
    static boolean isPalindrome(ListNode head) {
        ListNode mid = findMiddle(head);
        ListNode reversed = reverse(mid);   // reverse second half
        ListNode p1 = head, p2 = reversed;
        while (p2 != null) {
            if (p1.val != p2.val) return false;
            p1 = p1.next; p2 = p2.next;
        }
        return true;
    }

    // 8. Add two numbers (digits in reverse, return reversed sum)
    // Input: 2→4→3 (342), 5→6→4 (465)  → Output: 7→0→8 (807)
    static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), cur = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) { sum += l1.val; l1 = l1.next; }
            if (l2 != null) { sum += l2.val; l2 = l2.next; }
            carry = sum / 10;
            cur.next = new ListNode(sum % 10);
            cur = cur.next;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Reverse Linked List ===");
        // Input: 1→2→3→4→5
        System.out.println(print(reverse(build(1, 2, 3, 4, 5))));
        // Output: [5→4→3→2→1]

        System.out.println("\n=== 2. Detect Cycle ===");
        ListNode noCycle = build(1, 2, 3);
        ListNode withCycle = build(1, 2, 3, 4);
        withCycle.next.next.next.next = withCycle.next; // 4→2 creates cycle
        System.out.println(hasCycle(noCycle));   // false
        System.out.println(hasCycle(withCycle)); // true

        System.out.println("\n=== 3. Cycle Start ===");
        // (using same withCycle from above — cycle starts at node 2)
        ListNode start = cycleStart(withCycle);
        System.out.println(start != null ? "Cycle starts at node: " + start.val : "No cycle");
        // Output: Cycle starts at node: 2

        System.out.println("\n=== 4. Middle of List ===");
        // Input: 1→2→3→4→5
        System.out.println(findMiddle(build(1, 2, 3, 4, 5)).val); // 3
        System.out.println(findMiddle(build(1, 2, 3, 4)).val);    // 3 (second middle)

        System.out.println("\n=== 5. Merge Sorted Lists ===");
        // Input: 1→3→5, 2→4→6
        System.out.println(print(mergeSorted(build(1, 3, 5), build(2, 4, 6))));
        // Output: [1→2→3→4→5→6]

        System.out.println("\n=== 6. Remove 2nd from End ===");
        // Input: 1→2→3→4→5, n=2
        System.out.println(print(removeNthFromEnd(build(1, 2, 3, 4, 5), 2)));
        // Output: [1→2→3→5]

        System.out.println("\n=== 7. Palindrome ===");
        System.out.println(isPalindrome(build(1, 2, 3, 2, 1))); // true
        System.out.println(isPalindrome(build(1, 2, 3, 4, 5))); // false

        System.out.println("\n=== 8. Add Two Numbers ===");
        // Input: 2→4→3 (342) + 5→6→4 (465) = 807
        System.out.println(print(addTwoNumbers(build(2, 4, 3), build(5, 6, 4))));
        // Output: [7→0→8]
    }
}
