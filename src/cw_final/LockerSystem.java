package cw_final;

import java.util.*;
import cw_final.Graph;
import cw_final.PathResult;

public class LockerSystem {
    private final boolean[] isLocker;
    private final boolean[] occupied;
    private final String[] assignedTo;
    private final SimpleQueue<String> waitingQueue = new SimpleQueue<>();

    public LockerSystem(boolean[] lockerNodes) {
        int n = lockerNodes.length;
        isLocker = Arrays.copyOf(lockerNodes, n);
        occupied = new boolean[n];
        assignedTo = new String[n];
    }

    public boolean isLocker(int node) { return isLocker[node]; }
    public boolean isOccupied(int node) { return occupied[node]; }
    public String assignedTo(int node) { return assignedTo[node]; }

    //Nearest locker
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

    //Request a locker
    public String requestLocker(String user, Graph g, int src) {
        int locker = findNearestAvailableLocker(g, src);
        if (locker != -1) {
            occupied[locker] = true;
            assignedTo[locker] = user;
            int[] path = getPathToLocker(g, src, locker);
            double km = g.calculatePathDistance(path)/1000.0;
            return "Assigned " + g.getName(locker) + " to " + user + " | Distance: " + String.format("%.2f km", km);
        } else {
            waitingQueue.enqueue(user);
            return "All lockers full. " + user + " added to queue.";
        }
    }

    //Release a locker
    public String releaseLocker(int lockerId, Graph g, int src) {
        if(!isLocker[lockerId]) return "Not a locker.";
        if(!occupied[lockerId]) return "Locker already free.";

        String prevUser = assignedTo[lockerId];
        occupied[lockerId] = false;
        assignedTo[lockerId] = null;

        StringBuilder sb = new StringBuilder("Released " + g.getName(lockerId) + " (previously "+prevUser+").");

        if(!waitingQueue.isEmpty()) {
            String next = waitingQueue.dequeue();
            int newLock = findNearestAvailableLocker(g, src);
            if(newLock != -1){
                occupied[newLock]=true;
                assignedTo[newLock]=next;
                int[] path = getPathToLocker(g, src, newLock);
                double km = g.calculatePathDistance(path)/1000.0;
                sb.append(" Assigned ").append(g.getName(newLock))
                        .append(" to ").append(next)
                        .append(" | Distance: ").append(String.format("%.2f km", km));
            } else {
                waitingQueue.enqueue(next);
            }
        }
        return sb.toString();
    }

    public int[] getLockerIds() {
        int count = 0;
        for(boolean b:isLocker) if(b) count++;
        int[] ids = new int[count];
        int k=0;
        for(int i=0;i<isLocker.length;i++) if(isLocker[i]) ids[k++] = i;
        return ids;
    }

    public boolean hasWaiting() { return !waitingQueue.isEmpty(); }
}
