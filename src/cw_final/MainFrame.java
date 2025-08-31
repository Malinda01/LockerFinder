/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cw_final;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 *
 * @author USER
 */
public class MainFrame {
    
    JButton kruskalButton = new JButton("Run Kruskal");
    
    buttonPanel.add(kruskalButton);
    
    
    
    private void handleMST(){
        int n = graph.size();
        int[][] adj = graph.getAdjMatrix();

        // --- KRUSKAL MST from scratch (manual edge collect + bubble sort) ---
        List<int[]> edges = new ArrayList<>();
        for(int u=0; u<n; u++){
            for(int v=u+1; v<n; v++){
                if(adj[u][v] > 0) edges.add(new int[]{u,v,adj[u][v]});
            }
        }

        // bubble sort by weight
        for(int i=0;i<edges.size()-1;i++){
            for(int j=0;j<edges.size()-i-1;j++){
                if(edges.get(j)[2] > edges.get(j+1)[2]){
                    int[] temp = edges.get(j);
                    edges.set(j, edges.get(j+1));
                    edges.set(j+1, temp);
                }
            }
        }

        int[] parent = new int[n];
        for(int i=0;i<n;i++) parent[i] = i;

        List<int[]> mst = new ArrayList<>();
        for(int[] e : edges){
            int u = findParent(parent, e[0]);
            int v = findParent(parent, e[1]);
            if(u != v){
                mst.add(e);
                parent[u] = v;
            }
        }

        private void handleDFS() {
        int src = selectNode("Select starting node for DFS:");
        if (src == -1) return;

        String input = JOptionPane.showInputDialog(this, "Enter max distance (meters) to explore:");
        if (input == null || input.isEmpty()) return;

        int maxDist;
        try {
            maxDist = Integer.parseInt(input);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid distance.");
            return;
        }

        boolean[] visited = new boolean[graph.size()];
        List<Integer> reachableNodes = new ArrayList<>();
        List<int[]> reachableEdges = new ArrayList<>();
        dfsDistance(src, 0, maxDist, visited, reachableNodes, reachableEdges);

        GraphPanel popupPanel = new GraphPanel(graph, lockerSystem) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Highlight reachable edges
                g2.setColor(Color.ORANGE);
                g2.setStroke(new BasicStroke(3f));
                for (int[] e : reachableEdges) {
                    Point pu = graph.getPos(e[0]);
                    Point pv = graph.getPos(e[1]);
                    g2.drawLine(pu.x, pu.y, pv.x, pv.y);
                }

                // Keep track of label positions to avoid overlaps
                List<java.awt.Rectangle> labelRects = new ArrayList<>();
                FontMetrics fm = g2.getFontMetrics();

                // Highlight reachable nodes and draw labels
                for (int u : reachableNodes) {
                    Point p = graph.getPos(u);
                    g2.setColor(Color.MAGENTA);
                    g2.fillOval(p.x - 12, p.y - 12, 24, 24);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(p.x - 12, p.y - 12, 24, 24);

                    // Draw label without overlap
                    String label = graph.getName(u);
                    int labelWidth = fm.stringWidth(label);
                    int labelHeight = fm.getHeight();
                    int x = p.x - labelWidth / 2;
                    int y = p.y - 15;

                    java.awt.Rectangle rect = new java.awt.Rectangle(x, y - labelHeight, labelWidth, labelHeight);
                    int shift = 0;
                    while (labelRects.stream().anyMatch(r -> r.intersects(rect))) {
                        shift += labelHeight + 2; // shift down
                        rect.y = y - labelHeight + shift;
                    }
                    labelRects.add(rect);
                    g2.drawString(label, rect.x, rect.y + labelHeight);
                }

                g2.dispose();
            }
        };

        popupPanel.setPreferredSize(new Dimension(1000, 700));

        JOptionPane.showMessageDialog(this, new JScrollPane(popupPanel),
                "Reachable Locations from " + graph.getName(src), JOptionPane.INFORMATION_MESSAGE);
    }

    // DFS considering distance threshold
    private void dfsDistance(int u, int cumDist, int maxDist, boolean[] visited, List<Integer> nodes, List<int[]> edges) {
        visited[u] = true;
        nodes.add(u);
        for (int v = 0; v < graph.size(); v++) {
            int w = graph.getAdjMatrix()[u][v];
            if (w > 0 && !visited[v] && cumDist + w <= maxDist) {
                edges.add(new int[]{u, v});
                dfsDistance(v, cumDist + w, maxDist, visited, nodes, edges);
            }
        }
    }

        StringBuilder sb = new StringBuilder("MST edges (Kruskal, scratch):\n");
        for(int[] e : mst){
            sb.append(graph.getName(e[0]))
              .append(" - ")
              .append(graph.getName(e[1]))
              .append(" (").append(e[2]).append(")\n");
        }
        log(sb.toString());
    
    




    
}
