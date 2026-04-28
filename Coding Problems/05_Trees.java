import java.util.*;

/**
 * PATTERN: Binary Tree Traversals & Classic Problems
 *
 * Problems:
 *  1. Inorder   (Left → Root → Right)
 *  2. Preorder  (Root → Left → Right)
 *  3. Postorder (Left → Right → Root)
 *  4. Level order (BFS)
 *  5. Height of tree
 *  6. Check if BST is valid
 *  7. Lowest common ancestor (LCA)
 *  8. Diameter of binary tree
 *  9. Mirror / invert a tree
 * 10. Check if two trees are identical
 */
class Trees {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    // Helper to build tree from array (level order, null = missing)
    //        1
    //       / \
    //      2   3
    //     / \   \
    //    4   5   6
    static TreeNode buildTree(Integer[] arr) {
        if (arr == null || arr.length == 0) return null;
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < arr.length) {
            TreeNode cur = q.poll();
            if (i < arr.length && arr[i] != null) { cur.left = new TreeNode(arr[i]); q.offer(cur.left); }
            i++;
            if (i < arr.length && arr[i] != null) { cur.right = new TreeNode(arr[i]); q.offer(cur.right); }
            i++;
        }
        return root;
    }

    // 1. Inorder — Left Root Right
    // Tree: [1,2,3,4,5]  → Output: [4,2,5,1,3]
    static List<Integer> inorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }
    static void inorderHelper(TreeNode node, List<Integer> res) {
        if (node == null) return;
        inorderHelper(node.left, res);
        res.add(node.val);
        inorderHelper(node.right, res);
    }

    // 2. Preorder — Root Left Right
    // Tree: [1,2,3,4,5]  → Output: [1,2,4,5,3]
    static List<Integer> preorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorderHelper(root, result);
        return result;
    }
    static void preorderHelper(TreeNode node, List<Integer> res) {
        if (node == null) return;
        res.add(node.val);
        preorderHelper(node.left, res);
        preorderHelper(node.right, res);
    }

    // 3. Postorder — Left Right Root
    // Tree: [1,2,3,4,5]  → Output: [4,5,2,3,1]
    static List<Integer> postorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }
    static void postorderHelper(TreeNode node, List<Integer> res) {
        if (node == null) return;
        postorderHelper(node.left, res);
        postorderHelper(node.right, res);
        res.add(node.val);
    }

    // 4. Level order (BFS) — each level as a list
    // Tree: [1,2,3,4,5]  → Output: [[1],[2,3],[4,5]]
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;
        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int size = q.size();
            List<Integer> level = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode cur = q.poll();
                level.add(cur.val);
                if (cur.left != null)  q.offer(cur.left);
                if (cur.right != null) q.offer(cur.right);
            }
            result.add(level);
        }
        return result;
    }

    // 5. Height of tree
    // Tree: [1,2,3,4,5]  → Output: 3
    static int height(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(height(root.left), height(root.right));
    }

    // 6. Check if valid BST
    // BST rule: left < node < right (at every level)
    static boolean isValidBST(TreeNode root) {
        return isValidBSTHelper(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    static boolean isValidBSTHelper(TreeNode node, long min, long max) {
        if (node == null) return true;
        if (node.val <= min || node.val >= max) return false;
        return isValidBSTHelper(node.left, min, node.val) &&
               isValidBSTHelper(node.right, node.val, max);
    }

    // 7. Lowest Common Ancestor
    // Tree: [3,5,1,6,2,0,8], p=5, q=1  → Output: 3
    static TreeNode lca(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;
        TreeNode left  = lca(root.left, p, q);
        TreeNode right = lca(root.right, p, q);
        return (left != null && right != null) ? root : (left != null ? left : right);
    }

    // 8. Diameter (longest path between any two nodes)
    static int maxDiameter = 0;
    static int diameter(TreeNode root) {
        maxDiameter = 0;
        diameterHelper(root);
        return maxDiameter;
    }
    static int diameterHelper(TreeNode node) {
        if (node == null) return 0;
        int left = diameterHelper(node.left);
        int right = diameterHelper(node.right);
        maxDiameter = Math.max(maxDiameter, left + right);
        return 1 + Math.max(left, right);
    }

    // 9. Invert / mirror a tree
    static TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        TreeNode tmp = root.left;
        root.left  = invertTree(root.right);
        root.right = invertTree(tmp);
        return root;
    }

    // 10. Check if two trees are identical
    static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        return p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    public static void main(String[] args) {
        //        1
        //       / \
        //      2   3
        //     / \
        //    4   5
        TreeNode root = buildTree(new Integer[]{1, 2, 3, 4, 5});

        System.out.println("=== 1. Inorder (L-Root-R) ===");
        System.out.println(inorder(root));           // [4, 2, 5, 1, 3]

        System.out.println("\n=== 2. Preorder (Root-L-R) ===");
        System.out.println(preorder(root));          // [1, 2, 4, 5, 3]

        System.out.println("\n=== 3. Postorder (L-R-Root) ===");
        System.out.println(postorder(root));         // [4, 5, 2, 3, 1]

        System.out.println("\n=== 4. Level Order (BFS) ===");
        System.out.println(levelOrder(root));        // [[1], [2, 3], [4, 5]]

        System.out.println("\n=== 5. Height ===");
        System.out.println(height(root));            // 3

        System.out.println("\n=== 6. Is Valid BST ===");
        TreeNode bst = buildTree(new Integer[]{4, 2, 6, 1, 3, 5, 7});
        System.out.println(isValidBST(bst));         // true
        System.out.println(isValidBST(root));        // false (1,2,3,4,5 not a BST)

        System.out.println("\n=== 7. LCA of 4 and 5 ===");
        TreeNode p = root.left.left;   // 4
        TreeNode q = root.left.right;  // 5
        System.out.println(lca(root, p, q).val); // 2

        System.out.println("\n=== 8. Diameter ===");
        System.out.println(diameter(root));          // 3 (4→2→5 or 4→2→1→3)

        System.out.println("\n=== 9. Invert Tree (preorder after invert) ===");
        TreeNode inverted = invertTree(buildTree(new Integer[]{1, 2, 3, 4, 5}));
        System.out.println(preorder(inverted));      // [1, 3, 2, 5, 4]

        System.out.println("\n=== 10. Same Tree ===");
        TreeNode t1 = buildTree(new Integer[]{1, 2, 3});
        TreeNode t2 = buildTree(new Integer[]{1, 2, 3});
        TreeNode t3 = buildTree(new Integer[]{1, 2, 4});
        System.out.println(isSameTree(t1, t2));      // true
        System.out.println(isSameTree(t1, t3));      // false
    }
}
