package cw_final;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.table.AbstractTableModel;
import cw_final.Graph;

// ================= MAIN FRAME =================
class MainFrame extends JFrame{
    private final Graph graph;
    private final LockerSystem lockerSystem;
    private final GraphPanel graphPanel;
    private final JTextArea logArea = new JTextArea(8,40);
    private final JTable lockerTable;

    public MainFrame(Graph graph, LockerSystem lockerSystem, GraphPanel graphPanel){
        this.graph=graph;
        this.lockerSystem=lockerSystem;
        this.graphPanel=graphPanel;

        setTitle("SmartLocker Park System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton requestBtn = new JButton("Request Locker");
        JButton releaseBtn = new JButton("Release Locker");
        JButton dfsBtn = new JButton("Explore All Routes");
        JButton mstBtn = new JButton("Optimize Path Network");
        controlPanel.add(requestBtn);
        controlPanel.add(releaseBtn);
        controlPanel.add(dfsBtn);
        controlPanel.add(mstBtn);
        add(controlPanel, BorderLayout.NORTH);

        lockerTable = new JTable(new LockerTableModel(lockerSystem,graph));
        add(new JScrollPane(lockerTable), BorderLayout.EAST);

        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        // --- Button actions ---
        requestBtn.addActionListener(e -> handleRequest());
        releaseBtn.addActionListener(e -> handleRelease());
        dfsBtn.addActionListener(e -> handleDFS());
        mstBtn.addActionListener(e -> handleMST());

        pack();
        setLocationRelativeTo(null);
    }

    private void handleRequest(){
        String user = JOptionPane.showInputDialog(this, "Enter your name:");
        if(user != null && !user.isEmpty()){
            int src = selectNode("Select your current location:");
            if(src != -1){
                int previewLocker = lockerSystem.findNearestAvailableLocker(graph, src);
                if(previewLocker == -1){
                    log("All lockers are full. You will be added to the queue.");
                    lockerSystem.requestLocker(user, graph, src);
                    refreshTable();
                    return;
                }

                int[] pathPreview = lockerSystem.getPathToLocker(graph, src, previewLocker);
                graphPanel.setHighlightedPath(pathPreview);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Preview path to " + graph.getName(previewLocker) + ". Confirm assignment?",
                        "Confirm Locker", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION){
                    String msg = lockerSystem.requestLocker(user, graph, src);
                    log(msg);
                } else {
                    log("Locker assignment canceled by user.");
                }
                graphPanel.setHighlightedPath(new int[0]);
                refreshTable();
            }
        }
    }

    private void handleRelease(){
        int row = lockerTable.getSelectedRow();
        if(row != -1){
            int lid = ((LockerTableModel) lockerTable.getModel()).getLockerIdAt(row);
            int src = selectNode("Select your current location for nearest locker path:");
            if(src != -1){
                String msg = lockerSystem.releaseLocker(lid, graph, src);
                log(msg);
                int nextLocker = lockerSystem.findNearestAvailableLocker(graph, src);
                if(nextLocker != -1){
                    int[] nextPath = lockerSystem.getPathToLocker(graph, src, nextLocker);
                    graphPanel.setHighlightedPath(nextPath);
                } else {
                    graphPanel.setHighlightedPath(new int[0]);
                }
                refreshTable();
            }
        } else JOptionPane.showMessageDialog(this, "Select a locker first.");
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

        // --- DISTANCES TO LOCKERS (Dijkstra scratch + bubble sort) ---
        int[] lockerIds = lockerSystem.getLockerIds();
        StringBuilder distInfo = new StringBuilder("Shortest distances to lockers (sorted, scratch):\n\n");

        for(int src=0; src<n; src++){
            if(!lockerSystem.isLocker(src)){
                // Dijkstra scratch (again here for clarity)
                int[] dist = new int[n];
                boolean[] visited = new boolean[n];
                Arrays.fill(dist, Integer.MAX_VALUE);
                dist[src] = 0;

                for(int i=0;i<n;i++){
                    int u = -1, minDist = Integer.MAX_VALUE;
                    for(int j=0;j<n;j++){
                        if(!visited[j] && dist[j] < minDist){
                            minDist = dist[j];
                            u = j;
                        }
                    }
                    if(u==-1) break;
                    visited[u] = true;

                    for(int v=0;v<n;v++){
                        if(adj[u][v]>0 && dist[u] + adj[u][v] < dist[v]){
                            dist[v] = dist[u] + adj[u][v];
                        }
                    }
                }

                List<int[]> lockerDistances = new ArrayList<>();
                for(int lid : lockerIds) lockerDistances.add(new int[]{lid, dist[lid]});

                // bubble sort by distance
                for(int i=0;i<lockerDistances.size()-1;i++){
                    for(int j=0;j<lockerDistances.size()-i-1;j++){
                        if(lockerDistances.get(j)[1] > lockerDistances.get(j+1)[1]){
                            int[] temp = lockerDistances.get(j);
                            lockerDistances.set(j, lockerDistances.get(j+1));
                            lockerDistances.set(j+1, temp);
                        }
                    }
                }

                distInfo.append(graph.getName(src)).append(":\n");
                for(int[] ld : lockerDistances){
                    distInfo.append("   → ").append(graph.getName(ld[0]))
                            .append(" : ").append(ld[1]).append("\n");
                }
                distInfo.append("\n");
            }
        }

        JTextArea textArea = new JTextArea(distInfo.toString(), 25, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Distances to Lockers (Scratch)", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Union-Find helper ---
    private int findParent(int[] parent, int u){
        if(parent[u] != u) parent[u] = findParent(parent, parent[u]);
        return parent[u];
    }

    private void log(String msg){
        logArea.append(msg+"\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void refreshTable(){ ((AbstractTableModel)lockerTable.getModel()).fireTableDataChanged(); }

    private int selectNode(String title){
        String[] names = new String[graph.size()];
        for(int i=0;i<graph.size();i++) names[i]=graph.getName(i);
        String choice = (String)JOptionPane.showInputDialog(this,title,"Location",JOptionPane.PLAIN_MESSAGE,null,names,names[0]);
        if(choice!=null){
            for(int i=0;i<names.length;i++) if(names[i].equals(choice)) return i;
        }
        return -1;
    }

    // -------- APP FACTORY --------
    public static class AppFactory{
        public static MainFrame createApp(){
            Graph g = new Graph();
            // -------- NODES WITH FIXED POSITIONS --------
            int entranceW   = g.addNode("West Entrance", 120, 320);
            int mainPlaza   = g.addNode("Main Plaza (Hub)", 240, 320);
            int adventureJn = g.addNode("Adventure Junction", 360, 320);
            int fantasyFork = g.addNode("Fantasy Fork (Central)", 480, 320);
            int tomorrowBend= g.addNode("Tomorrow Bend", 600, 320);
            int exitE       = g.addNode("East Exit", 720, 320);

            int jungleGate  = g.addNode("Jungle Gate", 360, 220);
            int lagoonTurn  = g.addNode("Lagoon Turn", 360, 420);
            int castlePath  = g.addNode("Castle Path", 480, 220);
            int carouselWay = g.addNode("Carousel Way", 480, 420);

            int lockHubA    = g.addNode("Locker Station A (Hub North)", 240, 220);
            int lockHubB    = g.addNode("Locker Station B (Hub South)", 240, 420);
            int lockAdv     = g.addNode("Locker Station C (Adventure)", 360, 150);
            int lockFnt     = g.addNode("Locker Station D (Fantasy)", 480, 150);

            // -------- EDGES --------
            g.addUndirectedEdge(entranceW, mainPlaza);
            g.addUndirectedEdge(mainPlaza, adventureJn);
            g.addUndirectedEdge(adventureJn, fantasyFork);
            g.addUndirectedEdge(fantasyFork, tomorrowBend);
            g.addUndirectedEdge(tomorrowBend, exitE);

            g.addUndirectedEdge(adventureJn, jungleGate);
            g.addUndirectedEdge(adventureJn, lagoonTurn);
            g.addUndirectedEdge(fantasyFork, castlePath);
            g.addUndirectedEdge(fantasyFork, carouselWay);

            g.addUndirectedEdge(mainPlaza, lockHubA);
            g.addUndirectedEdge(mainPlaza, lockHubB);
            g.addUndirectedEdge(jungleGate, lockAdv);
            g.addUndirectedEdge(castlePath, lockFnt);

            // -------- LOCKER NODES --------
            boolean[] lockers = new boolean[g.size()];
            lockers[lockHubA]=true; lockers[lockHubB]=true;
            lockers[lockAdv]=true; lockers[lockFnt]=true;

            LockerSystem system = new LockerSystem(lockers);
            GraphPanel panel = new GraphPanel(g, system);
            return new MainFrame(g, system, panel);
        }
    }
}