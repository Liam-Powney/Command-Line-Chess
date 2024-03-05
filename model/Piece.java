package model;


public abstract class Piece {
    
    protected final boolean white;
    protected final int[][][] moves;
    protected final String type;

    // Constructors
    public Piece(boolean white, String type, int[][][] moves){
        this.white=white;
        this.type=type;
        this.moves=moves;
    }

    // clone constructor
    public Piece(Piece p) {
        this.white = p.getWhite();
        this.moves=p.getMoves();
        this.type=p.getType();
    }

    public boolean getWhite() {return white;}
    public String getType() {return type;}
    public int[][][] getMoves() {return moves;}

    @Override
    public boolean equals(Object obj) {
        if (this==obj) {return true;}
        if (obj==null || getClass()!=obj.getClass()) {return false;}
        Piece p = (Piece)obj;
        if (this.white==p.getWhite()) {return true;}
        return false;
    }
}
