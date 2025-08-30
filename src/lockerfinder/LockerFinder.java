package lockerfinder;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.SwingUtilities;

/**
 *
 * @authors
 * COHNDSE242F-015 : A.M.P.M.G.B Amarakoon
 * COHNDSE242F-016 : P.A.T.D.Gunawardhana
 * COHNDSE242F-017 : U.S.S.Udakanda
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

// ================= PATH RESULT =================
class PathResult {
    public final int[] dist;
    public final int[] prev;
    public PathResult(int[] dist, int[] prev) { this.dist = dist; this.prev = prev; }
}

// ================= LOCKER SYSTEM =================
class LockerSystem {
    private final boolean[] isLocker;
    private final boolean[] occupied;
    private final String[] assignedTo;
    private final Queue<String> waitingQueue = new LinkedList<>();

    public LockerSystem(boolean[] lockerNodes) {
        int n = lockerNodes.length;
        isLocker = Arrays.copyOf(lockerNodes, n);
        occupied = new boolean[n];
        assignedTo = new String[n];
    }

    public boolean isLocker(int node) { return isLocker[node]; }
    public boolean isOccupied(int node) { return occupied[node]; }
    public String assignedTo(int node) { return assignedTo[node]; }

    public int findNearestAvailableLocker(Graph g, int src) {
        PathResult pr = g.dijkstra(src);
        int best = -1, bestDist = Integer.MAX_VALUE;
        for (int i = 0; i < isLocker.length; i++) {
            if (isLocker[i] && !occupied[i] && pr.dist[i] < bestDist) {
                best = i; bestDist = pr.dist[i];
            }
        }
        return best;
    }

    public int[] getPathToLocker(Graph g, int src, int locker) {
        PathResult pr = g.dijkstra(src);
        return g.reconstructPath(locker, pr.prev);
    }

    public String requestLocker(String user, Graph g, int src) {
        int locker = findNearestAvailableLocker(g, src);
        if (locker != -1) {
            occupied[locker] = true;
            assignedTo[locker] = user;
            int[] path = getPathToLocker(g, src, locker);
            double km = g.calculatePathDistance(path)/1000.0;
            return "Assigned " + g.getName(locker) + " to " + user + " | Distance: " + String.format("%.2f km", km);
        } else {
            waitingQueue.add(user);
            return "All lockers full. " + user + " added to queue.";
        }
    }

// ------------------- UI CLASSES - Interface -------------------
class GraphPanel extends JPanel {
    private final Graph graph;
    private final LockerSystem lockerSystem;
    private int[] highlightedPath = new int[0];

    public GraphPanel(Graph graph, LockerSystem lockerSystem) {
        this.graph = graph;
        this.lockerSystem = lockerSystem;
        setPreferredSize(new Dimension(900, 500)); //Window height and width
        setBackground(Color.WHITE);
    }

    public void setHighlightedPath(int[] path) {
        highlightedPath = path != null ? path : new int[0];
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        for (int u = 0; u < graph.size(); u++) {
            Point pu = graph.getPos(u);
            for (int v = u + 1; v < graph.size(); v++) {
                if (graph.getAdjMatrix()[u][v] > 0) {
                    Point pv = graph.getPos(v);
                    boolean onPath = isEdgeOnPath(u, v);
                    g2.setStroke(new BasicStroke(onPath ? 3f : 1f));
                    g2.setColor(onPath ? Color.RED : Color.LIGHT_GRAY);
                    g2.drawLine(pu.x, pu.y, pv.x, pv.y);
                }
            }
        }

        // Draw nodes
        for (int i = 0; i < graph.size(); i++) {
            Point p = graph.getPos(i);
            boolean isLocker = lockerSystem.isLocker(i);
            boolean onPath = contains(highlightedPath, i);

            g2.setColor(onPath ? Color.RED : (isLocker ? new Color(0, 150, 0) : new Color(30, 90, 255)));
            g2.fillOval(p.x - 12, p.y - 12, 24, 24);

            // Draw border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(p.x - 12, p.y - 12, 24, 24);
        }

        // Draw labels separately to handle overlaps better
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 11f)); // Slightly smaller font
        FontMetrics fm = g2.getFontMetrics();
        
        // Create list of label positions to avoid overlaps
        java.util.List<Rectangle> usedAreas = new java.util.ArrayList<>();
        
        for (int i = 0; i < graph.size(); i++) {
            Point p = graph.getPos(i);
            String label = graph.getName(i);
            
            // Try to shorten very long labels
            String displayLabel = label;
            if (label.length() > 25) {
                displayLabel = label.substring(0, 22) + "...";
            }
            
            int labelWidth = fm.stringWidth(displayLabel);
            int labelHeight = fm.getHeight();
            
            // Try different positions for the label
            int[] yOffsets = {25, -8, 35, -18}; // Below, above, further below, further above
            int[] xOffsets = {0, -labelWidth/4, labelWidth/4}; // Center, left, right
            
            boolean placed = false;
            for (int yOff : yOffsets) {
                for (int xOff : xOffsets) {
                    int labelX = p.x - labelWidth/2 + xOff;
                    int labelY = p.y + yOff;
                    
                    Rectangle labelRect = new Rectangle(labelX - 2, labelY - labelHeight, 
                                                      labelWidth + 4, labelHeight + 2);
                    
                    // Check if this position overlaps with any existing label
                    boolean overlaps = false;
                    for (Rectangle used : usedAreas) {
                        if (labelRect.intersects(used)) {
                            overlaps = true;
                            break;
                        }
                    }
                    
                    if (!overlaps && labelX > 5 && labelX + labelWidth < getWidth() - 5 
                        && labelY > 5 && labelY < getHeight() - 5) {
                        // Draw the label
                        g2.drawString(displayLabel, labelX, labelY);
                        usedAreas.add(labelRect);
                        placed = true;
                        break;
                    }
                }
                if (placed) break;
            }
            
            // If we couldn't place it without overlap, just use the default position
            if (!placed) {
                int labelX = p.x - labelWidth/2;
                int labelY = p.y + 25;
                g2.drawString(displayLabel, labelX, labelY);
            }
        }

        g2.dispose();
    }

    private boolean contains(int[] arr, int x) {
        for (int v : arr) if (v == x) return true;
        return false;
    }

    private boolean isEdgeOnPath(int u, int v) {
        for (int i = 0; i < highlightedPath.length - 1; i++) {
            int a = highlightedPath[i], b = highlightedPath[i + 1];
            if ((a == u && b == v) || (a == v && b == u)) return true;
        }
        return false;
    }
}