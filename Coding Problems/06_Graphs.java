import java.util.*;

/**
 * PATTERN: Graph — BFS & DFS
 *
 * Graph represented as adjacency list: Map<Integer, List<Integer>>
 *
 * Problems:
 *  1. BFS — level-by-level traversal
 *  2. DFS — depth-first traversal
 *  3. Detect cycle in undirected graph
 *  4. Detect cycle in directed graph
 *  5. Shortest path (unweighted) — BFS
 *  6. Number of connected components
 *  7. Topological sort (DAG) — Kahn's algorithm
 *  8. Is graph bipartite (2-colorable)
 */
class Graphs {

    // Build adjacency list (undirected)
    static Map<Integer, List<Integer>> buildGraph(int[][] edges, int nodes) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < nodes; i++) graph.put(i, new ArrayList<>());
        for (int[] e : edges) {
            graph.get(e[0]).add(e[1]);
            graph.get(e[1]).add(e[0]);
        }
        return graph;
    }

    // 1. BFS — visit nodes level by level
    // Graph: 0-1, 0-2, 1-3, 2-4  start=0  → Output: [0, 1, 2, 3, 4]
    static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> result = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);
            for (int neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return result;
    }

    // 2. DFS — go as deep as possible before backtracking
    // Graph: 0-1, 0-2, 1-3, 2-4  start=0  → Output: [0, 1, 3, 2, 4]
    static List<Integer> dfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> result = new ArrayList<>();
        dfsHelper(graph, start, new HashSet<>(), result);
        return result;
    }
    static void dfsHelper(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited, List<Integer> result) {
        visited.add(node);
        result.add(node);
        for (int neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) dfsHelper(graph, neighbor, visited, result);
        }
    }

    // 3. Cycle in undirected graph
    // Graph with cycle: 0-1, 1-2, 2-0  → Output: true
    static boolean hasCycleUndirected(Map<Integer, List<Integer>> graph) {
        Set<Integer> visited = new HashSet<>();
        for (int node : graph.keySet()) {
            if (!visited.contains(node) && dfsCycleUndirected(graph, node, -1, visited)) return true;
        }
        return false;
    }
    static boolean dfsCycleUndirected(Map<Integer, List<Integer>> graph, int node, int parent, Set<Integer> visited) {
        visited.add(node);
        for (int neighbor : graph.get(node)) {
            if (!visited.contains(neighbor)) {
                if (dfsCycleUndirected(graph, neighbor, node, visited)) return true;
            } else if (neighbor != parent) return true; // back edge = cycle
        }
        return false;
    }

    // 4. Cycle in directed graph (using recursion stack)
    // Directed: 0→1, 1→2, 2→0  → Output: true
    static boolean hasCycleDirected(Map<Integer, List<Integer>> graph) {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recStack = new HashSet<>();
        for (int node : graph.keySet()) {
            if (dfsCycleDirected(graph, node, visited, recStack)) return true;
        }
        return false;
    }
    static boolean dfsCycleDirected(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited, Set<Integer> recStack) {
        if (recStack.contains(node)) return true;  // back edge
        if (visited.contains(node)) return false;
        visited.add(node);
        recStack.add(node);
        for (int neighbor : graph.get(node)) {
            if (dfsCycleDirected(graph, neighbor, visited, recStack)) return true;
        }
        recStack.remove(node);
        return false;
    }

    // 5. Shortest path (unweighted) — BFS
    // Graph: 0-1, 0-2, 1-3, 2-3  start=0, end=3  → Output: 2
    static int shortestPath(Map<Integer, List<Integer>> graph, int start, int end) {
        Queue<Integer> q = new LinkedList<>();
        Map<Integer, Integer> dist = new HashMap<>();
        q.offer(start);
        dist.put(start, 0);
        while (!q.isEmpty()) {
            int node = q.poll();
            if (node == end) return dist.get(node);
            for (int neighbor : graph.get(node)) {
                if (!dist.containsKey(neighbor)) {
                    dist.put(neighbor, dist.get(node) + 1);
                    q.offer(neighbor);
                }
            }
        }
        return -1; // unreachable
    }

    // 6. Count connected components
    // Graph: 0-1, 2-3 (0,1 connected; 2,3 connected; 4 isolated)  → Output: 3
    static int countComponents(Map<Integer, List<Integer>> graph) {
        Set<Integer> visited = new HashSet<>();
        int count = 0;
        for (int node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfsHelper(graph, node, visited, new ArrayList<>());
                count++;
            }
        }
        return count;
    }

    // 7. Topological sort (Kahn's BFS / in-degree method)
    // DAG: 5→0, 5→2, 4→0, 4→1, 2→3, 3→1  → Output: [4,5,2,3,0,1] (one valid order)
    static List<Integer> topologicalSort(Map<Integer, List<Integer>> graph, int nodes) {
        int[] inDegree = new int[nodes];
        for (int node : graph.keySet()) for (int nb : graph.get(node)) inDegree[nb]++;
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < nodes; i++) if (inDegree[i] == 0) q.offer(i);
        List<Integer> result = new ArrayList<>();
        while (!q.isEmpty()) {
            int node = q.poll();
            result.add(node);
            for (int nb : graph.get(node)) if (--inDegree[nb] == 0) q.offer(nb);
        }
        return result;
    }

    // 8. Is Bipartite (2-color with BFS)
    // Bipartite: 0-1, 1-2, 2-3, 3-0 (square)  → Output: true
    static boolean isBipartite(Map<Integer, List<Integer>> graph) {
        Map<Integer, Integer> color = new HashMap<>();
        for (int start : graph.keySet()) {
            if (color.containsKey(start)) continue;
            Queue<Integer> q = new LinkedList<>();
            q.offer(start);
            color.put(start, 0);
            while (!q.isEmpty()) {
                int node = q.poll();
                for (int nb : graph.get(node)) {
                    if (!color.containsKey(nb)) { color.put(nb, 1 - color.get(node)); q.offer(nb); }
                    else if (color.get(nb).equals(color.get(node))) return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // Graph: 0--1--3
        //        |     |
        //        2--4  |
        //             (4 also connects to 3 below)
        int[][] edges = {{0,1},{0,2},{1,3},{2,4}};
        Map<Integer, List<Integer>> graph = buildGraph(edges, 5);

        System.out.println("=== 1. BFS from 0 ===");
        System.out.println(bfs(graph, 0));           // [0, 1, 2, 3, 4]

        System.out.println("\n=== 2. DFS from 0 ===");
        System.out.println(dfs(graph, 0));           // [0, 1, 3, 2, 4]

        System.out.println("\n=== 3. Cycle in Undirected ===");
        Map<Integer, List<Integer>> cycleGraph = buildGraph(new int[][]{{0,1},{1,2},{2,0}}, 3);
        System.out.println(hasCycleUndirected(cycleGraph)); // true
        System.out.println(hasCycleUndirected(graph));      // false

        System.out.println("\n=== 4. Cycle in Directed ===");
        Map<Integer, List<Integer>> dg = new HashMap<>();
        for (int i = 0; i < 3; i++) dg.put(i, new ArrayList<>());
        dg.get(0).add(1); dg.get(1).add(2); dg.get(2).add(0); // 0→1→2→0 (cycle)
        System.out.println(hasCycleDirected(dg));    // true

        System.out.println("\n=== 5. Shortest Path 0→3 ===");
        Map<Integer, List<Integer>> g2 = buildGraph(new int[][]{{0,1},{0,2},{1,3},{2,3}}, 4);
        System.out.println(shortestPath(g2, 0, 3));  // 2

        System.out.println("\n=== 6. Connected Components ===");
        Map<Integer, List<Integer>> g3 = buildGraph(new int[][]{{0,1},{2,3}}, 5);
        System.out.println(countComponents(g3));     // 3 (0-1, 2-3, 4 alone)

        System.out.println("\n=== 7. Topological Sort ===");
        Map<Integer, List<Integer>> dag = new HashMap<>();
        for (int i = 0; i < 6; i++) dag.put(i, new ArrayList<>());
        dag.get(5).add(0); dag.get(5).add(2);
        dag.get(4).add(0); dag.get(4).add(1);
        dag.get(2).add(3); dag.get(3).add(1);
        System.out.println(topologicalSort(dag, 6)); // [4, 5, 0, 2, 3, 1]

        System.out.println("\n=== 8. Is Bipartite ===");
        Map<Integer, List<Integer>> square = buildGraph(new int[][]{{0,1},{1,2},{2,3},{3,0}}, 4);
        System.out.println(isBipartite(square));     // true
        Map<Integer, List<Integer>> triangle = buildGraph(new int[][]{{0,1},{1,2},{2,0}}, 3);
        System.out.println(isBipartite(triangle));   // false (odd cycle)
    }
}
