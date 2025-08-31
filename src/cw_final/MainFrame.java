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

        StringBuilder sb = new StringBuilder("MST edges (Kruskal, scratch):\n");
        for(int[] e : mst){
            sb.append(graph.getName(e[0]))
              .append(" - ")
              .append(graph.getName(e[1]))
              .append(" (").append(e[2]).append(")\n");
        }
        log(sb.toString());
    
    




    
}
