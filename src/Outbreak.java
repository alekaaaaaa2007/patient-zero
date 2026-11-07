import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections.
 */
public class Outbreak {

    /**
     * Mission 5a. Minimum Weight Vertex Cover on tree.
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        // Строим направленное дерево (корень 0) через BFS/DFS
        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int orderSize = 0;

        Queue<Integer> q = new ArrayDeque<>();
        q.add(0);
        visited[0] = true;

        while (!q.isEmpty()) {
            int u = q.poll();
            order[orderSize++] = u;
            for (int v : nb[u]) {
                if (!visited[v]) {
                    visited[v] = true;
                    children[u].add(v);
                    q.add(v);
                }
            }
        }

        // dp[u][0] - u НЕ тестируется
        // dp[u][1] - u ТЕСТИРУЕТСЯ
        long[] dp0 = new long[n];
        long[] dp1 = new long[n];

        // Снизу вверх от листьев к корню
        for (int i = n - 1; i >= 0; i--) {
            int u = order[i];
            dp1[u] = cost[u];
            dp0[u] = 0;

            for (int v : children[u]) {
                dp0[u] += dp1[v];                     // если u не тестируется, все дети обязаны
                dp1[u] += Math.min(dp0[v], dp1[v]);  // если u тестируется, дети могут выбираться свободно
            }
        }

        // Восстановление ответа (сверху вниз от корня)
        boolean[] tested = new boolean[n];
        tested[0] = dp1[0] <= dp0[0];

        for (int i = 0; i < n; i++) {
            int u = order[i];
            if (!tested[u]) {
                for (int v : children[u]) {
                    tested[v] = true;
                }
            } else {
                for (int v : children[u]) {
                    tested[v] = dp1[v] < dp0[v];
                }
            }
        }
        return tested;
    }

    /**
     * Mission 5b. Longest chain (diameter of tree) in links.
     */
    public static int longestChain(int n, int[][] nb) {
        List<Integer>[] children = new ArrayList[n];
        for (int i = 0; i < n; i++) children[i] = new ArrayList<>();

        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int orderSize = 0;

        Queue<Integer> q = new ArrayDeque<>();
        q.add(0);
        visited[0] = true;

        while (!q.isEmpty()) {
            int u = q.poll();
            order[orderSize++] = u;
            for (int v : nb[u]) {
                if (!visited[v]) {
                    visited[v] = true;
                    children[u].add(v);
                    q.add(v);
                }
            }
        }

        int[] maxDown = new int[n];
        int maxChain = 0;

        for (int i = n - 1; i >= 0; i--) {
            int u = order[i];
            int firstMax = 0, secondMax = 0;

            for (int v : children[u]) {
                int h = maxDown[v] + 1;
                if (h > firstMax) {
                    secondMax = firstMax;
                    firstMax = h;
                } else if (h > secondMax) {
                    secondMax = h;
                }
            }
            maxDown[u] = firstMax;
            maxChain = Math.max(maxChain, firstMax + secondMax);
        }
        return maxChain;
    }
}