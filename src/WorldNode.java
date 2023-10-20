public class WorldNode {
    private int dfs;  // g
    private int dte; // h
    private int td;  // f
    private Point position;
    private WorldNode parent;


    // getter methods
    public int getTd() {
        return td;
    }

    public int getDfs() {
        return dfs;
    }

    public int getDte() {
        return dte;
    }

    public Point getPosition() {
        return position;
    }

    public WorldNode getParent() {
        return parent;
    }
    // setter methods

    public void setTd(int td) {
        this.td = td;
    }

    public WorldNode(int dfs, int dte, Point position, WorldNode parent){
        this.dfs = dfs;
        this.dte = dte;
        this.td = this.dfs + this.dte;
        this.position = position;
        this.parent = parent;
    }

//    public boolean adjacent(WorldNode n2){
//        return this.position.adjacent(n2.position);
//    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof WorldNode n2) {
            return this.position.equals(n2.getPosition());
        } return false;
    }

    @Override
    public int hashCode() {
        return this.getPosition().x();
    }

}
