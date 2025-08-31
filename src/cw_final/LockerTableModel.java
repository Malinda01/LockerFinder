package cw_final;

// ================= LOCKER TABLE =================

import javax.swing.table.AbstractTableModel;

class LockerTableModel extends AbstractTableModel{
    private final LockerSystem lockerSystem;
    private final Graph graph;
    private final int[] lockerIds;
    private final String[] columns = {"Locker","Occupied By"};

    public LockerTableModel(LockerSystem lockerSystem, Graph graph){
        this.lockerSystem=lockerSystem;
        this.graph=graph;
        this.lockerIds = lockerSystem.getLockerIds();
    }

    @Override
    public int getRowCount() { return lockerIds.length; }
    @Override
    public int getColumnCount() { return columns.length; }
    @Override
    public String getColumnName(int col){ return columns[col]; }

    @Override
    public Object getValueAt(int row, int col){
        int lid = lockerIds[row];
        if(col==0) return graph.getName(lid);
        if(col==1) return lockerSystem.isOccupied(lid)?lockerSystem.assignedTo(lid):"Free";
        return "";
    }

    public int getLockerIdAt(int row){ return lockerIds[row]; }
}