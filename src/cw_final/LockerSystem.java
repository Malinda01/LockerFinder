package newpackage;

import java.util.*;
import newpackage.Graph;
import newpackage.PathResult;

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
    