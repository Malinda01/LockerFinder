package cw_final;

import java.awt.Point;
import java.util.*;
import cw_final.PathResult;

public class Graph {
    private final int MAX_NODES = 50;
    private int nodeCount = 0;
    private String[] names = new String[MAX_NODES];
    private Point[] positions = new Point[MAX_NODES];
    private int[][] adjMatrix = new int[MAX_NODES][MAX_NODES];
    
    //Add a node
    public int addNode(String name, int x, int y) {
        names[nodeCount] = name;
        positions[nodeCount] = new Point(x, y);
        for (int j = 0; j < MAX_NODES; j++) adjMatrix[nodeCount][j] = 0;
        return nodeCount++;
    }

    //Edges
    public void addUndirectedEdge(int u, int v) {
        int w = (int)Math.round(positions[u].distance(positions[v]));
        adjMatrix[u][v] = w;
        adjMatrix[v][u] = w;
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
}
