package lockerfinder;

import java.awt.Point;
import java.util.Arrays;
import javax.swing.SwingUtilities;

/**
 *
 * @authors
 * COHNDSE242F-015 : A.M.P.M.G.B Amarakoon
 * COHNDSE242F-016 : 
 * COHNDSE242F-017 : 
 * COHNDSE242F-018 : M.M.P.N Munasinghe
 * 
 */
public class LockerFinder {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = MainFrame.AppFactory.createApp();
            frame.setVisible(true);
        });
    }
}

// ------------------- GRAPH CLASS -------------------
class Graph {
    private final int MAX_NODES = 200;
    private int nodeCount = 0;
    private String[] names = new String[MAX_NODES];
    private Point[] positions = new Point[MAX_NODES]; // Store X,Y coordinates for drawing nodes on the map
    private int[][] adjMatrix = new int[MAX_NODES][MAX_NODES]; // Edge weights

    public int addNode(String name, int x, int y) {
        names[nodeCount] = name;
        positions[nodeCount] = new Point(x, y);
        for (int j = 0; j < MAX_NODES; j++) adjMatrix[nodeCount][j] = 0;
        return nodeCount++;
    }

    public void addUndirectedEdge(int u, int v) {
        int w = distance(u, v);
        adjMatrix[u][v] = w;
        adjMatrix[v][u] = w;
    }

    private int distance(int u, int v) {
        Point a = positions[u], b = positions[v];
        return (int) Math.round(Math.hypot(a.x - b.x, a.y - b.y));
    }

    public int size() { return nodeCount; }
    public String getName(int i) { return names[i]; }
    public Point getPos(int i) { return positions[i]; }
    public int[][] getAdjMatrix() { return adjMatrix; }

    // -------- Dijkstra --------
    public PathResult dijkstra(int src) {
        int n = nodeCount;
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[src] = 0;

        for (int i = 0; i < n; i++) {
            int u = -1, best = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[j] < best) { best = dist[j]; u = j; }
            }
            if (u == -1) break;
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (adjMatrix[u][v] > 0 && dist[u] + adjMatrix[u][v] < dist[v]) {
                    dist[v] = dist[u] + adjMatrix[u][v];
                    prev[v] = u;
                }
            }
        }
        return new PathResult(dist, prev);
    }

    public int[] reconstructPath(int dest, int[] prev) {
        int[] path = new int[nodeCount];
        int len = 0, cur = dest;
        while (cur != -1) { path[len++] = cur; cur = prev[cur]; }
        int[] result = new int[len];
        for (int i = 0; i < len; i++) result[i] = path[len - 1 - i];
        return result;
    }

    // -------- Path distance calculation --------
    public int calculatePathDistance(int[] path) {
        int dist = 0;
        for (int i = 0; i < path.length - 1; i++) {
            dist += adjMatrix[path[i]][path[i + 1]];
        }
        return dist;
    }
}
