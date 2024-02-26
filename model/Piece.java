package model;


public abstract class Piece {
    
    protected boolean white;
    protected boolean hasMoved;
    protected final int[][][] moves;
    protected String type;

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
    }

    public boolean getWhite() {return white;}
    public boolean getHasMoved() {return hasMoved;}
    public void setMoved() {hasMoved = true;}
    public String getType() {return type;}
    public int[][][] getMoves() {return moves;}


}
