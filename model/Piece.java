package model;


public abstract class Piece {
    
    protected final boolean white;
    protected boolean hasMoved;
    protected final int[][][] moves;
    protected final String type;

    // Constructors
    public Piece(boolean white, String type, int[][][] moves){
        this.hasMoved=false;
        this.white=white;
        this.type=type;
        this.moves=moves;
    }

    // clone constructor
    public Piece(Piece p) {
        this.white = p.getWhite();
        this.hasMoved=p.getHasMoved();
        this.moves=p.getMoves();
        this.type=p.getType();
    }

    public boolean getWhite() {return white;}
    public boolean getHasMoved() {return hasMoved;}
    public String getType() {return type;}
    public int[][][] getMoves() {return moves;}

    public void setMoved(Move m) {hasMoved = true;}
}
