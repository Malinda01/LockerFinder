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

    //Path recons
    public int[] reconstructPath(int dest, int[] prev) {
        int[] temp = new int[nodeCount];
        int len = 0, cur = dest;
        while(cur != -1) { temp[len++] = cur; cur = prev[cur]; }
        int[] path = new int[len];
        for(int i=0;i<len;i++) path[i]=temp[len-1-i];
        return path;
    }

    // Path Distance calc
    public int calculatePathDistance(int[] path) {
        int dist = 0;
        for(int i=0;i<path.length-1;i++) dist += adjMatrix[path[i]][path[i+1]];
        return dist;
    }

    // -------- DFS --------
    public List<Integer> dfs(int src){
        boolean[] visited = new boolean[nodeCount];
        List<Integer> order = new ArrayList<>();
        dfsHelper(src, visited, order);
        return order;
    }
    private void dfsHelper(int u, boolean[] visited, List<Integer> order){
        visited[u]=true;
        order.add(u);
        for(int v=0; v<nodeCount; v++){
            if(adjMatrix[u][v]>0 && !visited[v]){
                dfsHelper(v, visited, order);
            }
        }
    }

    // -------- Kruskal --------
    public List<int[]> kruskalMST(){
        List<int[]> edges = new ArrayList<>();
        for(int u=0; u<nodeCount; u++){
            for(int v=u+1; v<nodeCount; v++){
                if(adjMatrix[u][v]>0) edges.add(new int[]{u,v,adjMatrix[u][v]});
            }
        }
        // bubble sort
        for (int i = 0; i < edges.size() - 1; i++) {
            for (int j = 0; j < edges.size() - i - 1; j++) {
                if (edges.get(j)[2] > edges.get(j+1)[2]) {
                    int[] tmp = edges.get(j);
                    edges.set(j, edges.get(j+1));
                    edges.set(j+1, tmp);
                }
            }
        }
        int[] parent = new int[nodeCount];
        for(int i=0;i<nodeCount;i++) parent[i]=i;

        List<int[]> mst = new ArrayList<>();
        for(int[] e: edges){
            int u=find(parent,e[0]), v=find(parent,e[1]);
            if(u!=v){
                mst.add(e);
                parent[u]=v;
            }
        }
        return mst;
    }
    private int find(int[] parent,int u){
        if(parent[u]!=u) parent[u]=find(parent,parent[u]);
        return parent[u];
    }
}
