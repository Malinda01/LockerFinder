package cw_final;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import cw_final.Graph;

// ================= GRAPH PANEL =================
class GraphPanel extends JPanel {
    private final Graph graph;
    private final LockerSystem lockerSystem;
    private int[] highlightedPath = new int[0];

    public GraphPanel(Graph graph, LockerSystem lockerSystem){
        this.graph = graph;
        this.lockerSystem = lockerSystem;
        setPreferredSize(new Dimension(900,500));
        setBackground(Color.WHITE);
    }

    public void setHighlightedPath(int[] path){
        highlightedPath = path!=null? path:new int[0];
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges with distance labels
        for(int u=0; u<graph.size(); u++){
            Point pu = graph.getPos(u);
            for(int v=u+1; v<graph.size(); v++){
                if(graph.getAdjMatrix()[u][v] > 0){
                    Point pv = graph.getPos(v);
                    boolean onPath = isEdgeOnPath(u,v);
                    g2.setStroke(new BasicStroke(onPath?3f:1f));
                    g2.setColor(onPath?Color.RED:Color.LIGHT_GRAY);
                    g2.drawLine(pu.x, pu.y, pv.x, pv.y);

                    int midX = (pu.x + pv.x)/2;
                    int midY = (pu.y + pv.y)/2;
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    g2.drawString(String.valueOf(graph.getAdjMatrix()[u][v]), midX, midY);
                }
            }
        }

        // Draw nodes
        for(int i=0;i<graph.size();i++){
            Point p = graph.getPos(i);
            boolean isLocker = lockerSystem.isLocker(i);
            boolean onPath = contains(highlightedPath,i);

            g2.setColor(onPath?Color.RED:(isLocker?new Color(0,150,0):new Color(30,90,255)));
            g2.fillOval(p.x-12,p.y-12,24,24);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(p.x-12,p.y-12,24,24);
        }

        // Draw labels
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,11f));
        FontMetrics fm = g2.getFontMetrics();
        for(int i=0;i<graph.size();i++){
            Point p = graph.getPos(i);
            String label = graph.getName(i);
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label,p.x-labelWidth/2,p.y-15);
        }
        g2.dispose();
    }

    private boolean contains(int[] arr,int x){ for(int v:arr) if(v==x) return true; return false; }
    private boolean isEdgeOnPath(int u,int v){
        for(int i=0;i<highlightedPath.length-1;i++){
            int a=highlightedPath[i],b=highlightedPath[i+1];
            if((a==u && b==v)||(a==v && b==u)) return true;
        }
        return false;
    }
}