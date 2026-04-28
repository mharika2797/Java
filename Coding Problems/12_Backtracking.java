import java.util.*;

/**
 * PATTERN: Backtracking
 *
 * Core idea: build a solution incrementally, abandon ("backtrack")
 * a path as soon as it's known to not lead to a valid solution.
 * Template:
 *   void backtrack(state) {
 *       if (done) { save result; return; }
 *       for (choice : choices) {
 *           make choice;
 *           backtrack(next state);
 *           undo choice;        ← backtrack
 *       }
 *   }
 *
 * Problems:
 *  1. All permutations of an array
 *  2. All subsets (power set)
 *  3. Combination sum (reuse allowed)
 *  4. Letter combinations of phone number
 *  5. N-Queens
 *  6. Word search in grid
 */
class Backtracking {

    // 1. All permutations
    // Input: [1,2,3]  → Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        permuteHelper(nums, new boolean[nums.length], new ArrayList<>(), result);
        return result;
    }
    static void permuteHelper(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == nums.length) { result.add(new ArrayList<>(current)); return; }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            current.add(nums[i]);
            permuteHelper(nums, used, current, result);
            current.remove(current.size() - 1); // undo
            used[i] = false;
        }
    }

    // 2. All subsets (power set)
    // Input: [1,2,3]  → Output: [[],[1],[1,2],[1,2,3],[1,3],[2],[2,3],[3]]
    static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        subsetsHelper(nums, 0, new ArrayList<>(), result);
        return result;
    }
    static void subsetsHelper(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            subsetsHelper(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // 3. Combination sum — numbers can be reused
    // Input: candidates=[2,3,6,7], target=7  → Output: [[2,2,3],[7]]
    static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        combinationHelper(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }
    static void combinationHelper(int[] cands, int remaining, int start, List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) { result.add(new ArrayList<>(current)); return; }
        for (int i = start; i < cands.length; i++) {
            if (cands[i] > remaining) break; // pruning
            current.add(cands[i]);
            combinationHelper(cands, remaining - cands[i], i, current, result); // i = reuse allowed
            current.remove(current.size() - 1);
        }
    }

    // 4. Letter combinations of a phone number
    // Input: "23"  → Output: [ad,ae,af,bd,be,bf,cd,ce,cf]
    static List<String> letterCombinations(String digits) {
        if (digits.isEmpty()) return new ArrayList<>();
        String[] map = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
        List<String> result = new ArrayList<>();
        letterHelper(digits, map, 0, new StringBuilder(), result);
        return result;
    }
    static void letterHelper(String digits, String[] map, int i, StringBuilder cur, List<String> result) {
        if (i == digits.length()) { result.add(cur.toString()); return; }
        for (char c : map[digits.charAt(i) - '0'].toCharArray()) {
            cur.append(c);
            letterHelper(digits, map, i + 1, cur, result);
            cur.deleteCharAt(cur.length() - 1);
        }
    }

    // 5. N-Queens — place N queens on NxN board, no two attack each other
    // Input: n=4  → Output: 2 solutions
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        nQueensHelper(board, 0, result);
        return result;
    }
    static void nQueensHelper(char[][] board, int row, List<List<String>> result) {
        if (row == board.length) {
            List<String> solution = new ArrayList<>();
            for (char[] r : board) solution.add(new String(r));
            result.add(solution);
            return;
        }
        for (int col = 0; col < board.length; col++) {
            if (isSafe(board, row, col)) {
                board[row][col] = 'Q';
                nQueensHelper(board, row + 1, result);
                board[row][col] = '.';
            }
        }
    }
    static boolean isSafe(char[][] board, int row, int col) {
        for (int i = 0; i < row; i++) if (board[i][col] == 'Q') return false;
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) if (board[i][j] == 'Q') return false;
        for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) if (board[i][j] == 'Q') return false;
        return true;
    }

    // 6. Word search in grid
    // Input: board=[["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word="ABCCED"  → true
    static boolean wordSearch(char[][] board, String word) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++)
                if (wordDFS(board, word, i, j, 0)) return true;
        return false;
    }
    static boolean wordDFS(char[][] board, String word, int i, int j, int k) {
        if (k == word.length()) return true;
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || board[i][j] != word.charAt(k)) return false;
        char tmp = board[i][j];
        board[i][j] = '#'; // mark visited
        boolean found = wordDFS(board, word, i+1, j, k+1) || wordDFS(board, word, i-1, j, k+1)
                      || wordDFS(board, word, i, j+1, k+1) || wordDFS(board, word, i, j-1, k+1);
        board[i][j] = tmp; // restore
        return found;
    }

    public static void main(String[] args) {
        System.out.println("=== 1. Permutations of [1,2,3] ===");
        permute(new int[]{1, 2, 3}).forEach(System.out::println);
        // [1,2,3] [1,3,2] [2,1,3] [2,3,1] [3,1,2] [3,2,1]

        System.out.println("\n=== 2. Subsets of [1,2,3] ===");
        subsets(new int[]{1, 2, 3}).forEach(System.out::println);
        // [] [1] [1,2] [1,2,3] [1,3] [2] [2,3] [3]

        System.out.println("\n=== 3. Combination Sum target=7 ===");
        combinationSum(new int[]{2, 3, 6, 7}, 7).forEach(System.out::println);
        // [2,2,3] [7]

        System.out.println("\n=== 4. Letter Combinations '23' ===");
        System.out.println(letterCombinations("23"));
        // [ad, ae, af, bd, be, bf, cd, ce, cf]

        System.out.println("\n=== 5. N-Queens n=4 (" + solveNQueens(4).size() + " solutions) ===");
        solveNQueens(4).forEach(s -> { s.forEach(System.out::println); System.out.println(); });

        System.out.println("=== 6. Word Search 'ABCCED' ===");
        char[][] board = {{'A','B','C','E'},{'S','F','C','S'},{'A','D','E','E'}};
        System.out.println(wordSearch(board, "ABCCED")); // true
        System.out.println(wordSearch(board, "ABCB"));   // false
    }
}
